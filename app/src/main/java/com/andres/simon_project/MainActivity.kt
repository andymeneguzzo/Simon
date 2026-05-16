package com.andres.simon_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    /* Global variables for UI elements */

    /* non-editable TextView printing the clicked sequence */
    private lateinit var textViewSequence: TextView

    /* Colored Buttons (actually clickable TextViews) */
    private lateinit var buttonRed: TextView
    private lateinit var buttonGreen: TextView
    private lateinit var buttonBlue: TextView
    private lateinit var buttonMagenta: TextView
    private lateinit var buttonYellow: TextView
    private lateinit var buttonCyan: TextView

    /* Buttons */
    private lateinit var buttonStartGame: Button
    private lateinit var buttonPauseGame: Button
    private lateinit var buttonEndOfGame: Button

    /* AudioTrack audio player */
    private val audioPlayer = SimonAudioPlayer()

    /* As suggested, use kotlin coroutine to handle computer sequence presentation */
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var presentationJob: Job? = null

    /* Database */
    private lateinit var dbHelper: MatchDBHelper

    /* Declare a companion object to identify the useful information for InstanceState preservation */
    companion object {
        /* current match data */
        private const val ID_CURRENT_SEQUENCE = "id_current_sequence"
        private const val ID_COMPUTER_SEQUENCE = "id_computer_sequence"
        private const val ID_GAME_STATE = "id_game_state"
        private const val ID_COMPUTER_PRESENTATION_INDEX = "id_computer_presentation_index"
        private const val ID_MAX_CORRECT_LENGTH = "id_max_correct_length"

        /* match yet to be saved data */
        private const val ID_HAS_PENDING_MATCH = "id_has_pending_match"
        private const val ID_PENDING_MATCH_SEQUENCE = "id_pending_match_sequence"
        private const val ID_PENDING_MATCH_ERROR_INDEX = "id_pending_match_error_index"
        private const val ID_PENDING_MATCH_MAX_CORRECT = "id_pending_match_max_correct"
        private const val ID_PENDING_MATCH_CREATED_AT = "id_pending_match_created_at"
    }

    /* onCreate method being called at app launch */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /* First screen shown is the HomeScreen (declared in Manifest) */
        setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = MatchDBHelper(this)

        /* Bind variables with their UI elements (found by ID) */
        bindUIViews()

        /* Ensure instance state is preserved when orientation changes */
        handleInstanceState(savedInstanceState)

        /* Set interaction listeners for clickable colored TextViews and Buttons  */
        setInteractionListeners()

        printGameText()

        /* TODO will have to deal with update of button state */

        if (GameSession.gameState == GameSession.GameState.COMPUTER_TURN) {
            /* TODO : start the computer presentation */
        }
    }

    /* called for example when the user from MatchListActivity pressed the back button and navigates back Home Screen */
    override fun onResume() {
        super.onResume()
        printGameText()
    }

    override fun onDestroy() {
        super.onDestroy()
        // coroutine and job canceled
        presentationJob?.cancel()
        gameScope.cancel()
    }

    /* Overridden method for Instance State saving */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        /* put the current sequence */
        outState.putStringArrayList(ID_CURRENT_SEQUENCE, ArrayList(GameSession.currentSequence))
        outState.putStringArrayList(ID_COMPUTER_SEQUENCE, ArrayList(GameSession.currentSequence))
        outState.putString(ID_GAME_STATE, GameSession.gameState.name)
        outState.putInt(ID_COMPUTER_PRESENTATION_INDEX, GameSession.computerPresentationIndex)
        outState.putInt(ID_MAX_CORRECT_LENGTH, GameSession.maxCorrectLength)

        // put the pending match if there exists one
        val pendingMatch = GameSession.matchYetToBeSaved
        outState.putBoolean(ID_HAS_PENDING_MATCH, pendingMatch != null)
        if (pendingMatch != null) {
            outState.putStringArrayList(ID_PENDING_MATCH_SEQUENCE, ArrayList(pendingMatch.errorSequence))
            outState.putInt(ID_PENDING_MATCH_ERROR_INDEX, pendingMatch.errorIndex)
            outState.putInt(ID_PENDING_MATCH_MAX_CORRECT, pendingMatch.maxCorrectLength)
            outState.putLong(ID_PENDING_MATCH_CREATED_AT, pendingMatch.createdAt)
        }
    }

    private fun handleInstanceState(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) return

        /* get the current sequence associated to the ID_CURRENT_SEQUENCE companion object */
        val handledCurrentSequence = savedInstanceState.getStringArrayList(ID_CURRENT_SEQUENCE) ?: arrayListOf()
        val handledComputerSequence = savedInstanceState.getStringArrayList(ID_COMPUTER_SEQUENCE) ?: arrayListOf()
        val handledGameStateName = savedInstanceState.getString(ID_GAME_STATE) ?: GameSession.GameState.IDLE.name
        val handledGameState = GameSession.GameState.valueOf(handledGameStateName)
        val handledComputerPresentationIndex = savedInstanceState.getInt(ID_COMPUTER_PRESENTATION_INDEX)
        val handledMaxCorrectLength = savedInstanceState.getInt(ID_MAX_CORRECT_LENGTH)

        // pending match data
        val hasPendingMatch = savedInstanceState.getBoolean(ID_HAS_PENDING_MATCH)
        val handledPendingMatch = if (hasPendingMatch) {
            val pendingSequence = savedInstanceState.getStringArrayList(
                ID_PENDING_MATCH_SEQUENCE
            ) ?: arrayListOf()
            GameSession.Match(
                maxCorrectLength = savedInstanceState.getInt(ID_PENDING_MATCH_MAX_CORRECT),
                errorSequence = pendingSequence,
                errorIndex = savedInstanceState.getInt(ID_PENDING_MATCH_ERROR_INDEX),
                createdAt = savedInstanceState.getLong(ID_PENDING_MATCH_CREATED_AT)
            )
        } else {
            null
        }

        GameSession.restoreGameState(
            computerSequence = handledComputerSequence,
            currentSequence = handledCurrentSequence,
            gameState = handledGameState,
            computerPresentationIndex = handledComputerPresentationIndex,
            maxCorrectLength = handledMaxCorrectLength,
            matchYetToBeSaved = handledPendingMatch
        )
    }

    private fun bindUIViews() {
        /* TextView for sequence */
        textViewSequence = findViewById(R.id.textViewSequence)

        /* Colored clickable TextViews */
        buttonRed = findViewById(R.id.buttonRed)
        buttonGreen = findViewById(R.id.buttonGreen)
        buttonBlue = findViewById(R.id.buttonBlue)
        buttonMagenta = findViewById(R.id.buttonMagenta)
        buttonYellow = findViewById(R.id.buttonYellow)
        buttonCyan = findViewById(R.id.buttonCyan)

        /* Buttons */
        buttonStartGame = findViewById(R.id.buttonStartGame)
        buttonPauseGame = findViewById(R.id.buttonPauseGame)
        buttonEndOfGame = findViewById(R.id.buttonEndOfGame)
    }

    private fun setInteractionListeners() {
        /* For colored TextViews, when clicked the onColorPressed method is called
        * to handle the click of the specific color  */
        buttonRed.setOnClickListener { onColorClicked("R") }
        buttonGreen.setOnClickListener { onColorClicked("G") }
        buttonBlue.setOnClickListener { onColorClicked("B") }
        buttonMagenta.setOnClickListener { onColorClicked("M") }
        buttonYellow.setOnClickListener { onColorClicked("Y") }
        buttonCyan.setOnClickListener { onColorClicked("C") }

        /* Buttons */
        buttonStartGame.setOnClickListener {
            GameSession.startNewGame()
            /* TODO: complete */
        }

        buttonPauseGame.setOnClickListener {
            /* TODO: handle pause game */
        }

        buttonEndOfGame.setOnClickListener {
            /* TODO: handle end of game */
        }
    }
    private fun onColorClicked(colorName: String) {
        GameSession.appendColor(colorName) // add color to the sequence
        printCurrentSequence()
    }

    /*
    private fun printCurrentSequence() {
        // now that sequenceLabel is in strings for multilingual support, must retrieve it with getString(...)
        val sequenceLabel = getString(R.string.sequence_label)
        var sequenceText = sequenceLabel

        /* now that sequence_label is in strings for italian and english support
        * the text shown in the TextView will adapt it's language based on system language */
        if (!GameSession.currentSequence.isEmpty()) {
            sequenceText = "$sequenceLabel: ${GameSession.currentSequence.joinToString(", ")}"
        }

        /* if not empty, will visualize the sequence of colors separated by comma, otherwise the
        * empty sequence will be printed */
        textViewSequence.text = sequenceText
    }
    */

    private fun printGameText() {
        when (GameSession.gameState) {
            GameSession.GameState.IDLE -> {
                textViewSequence.text = "" // nothing to show, in IDLE mode
            }

            GameSession.GameState.COMPUTER_TURN, // here it's the computer presenting the sequence
            GameSession.GameState.PAUSED -> {
                textViewSequence.text = ""
            }
            GameSession.GameState.PLAYER_TURN -> {
                val sequenceLabel = getString(R.string.sequence_label)
                textViewSequence.text = if (GameSession.currentSequence.isEmpty()) {
                    sequenceLabel
                } else {
                    "$sequenceLabel ${GameSession.currentSequence.joinToString(", ")}"
                }
            }

            GameSession.GameState.GAME_OVER -> {
                printErrorState()
            }
        }
    }
    private fun printErrorState() {
        textViewSequence.text = getString(R.string.game_error)
    }
    private fun updateButtonState() {
        buttonStartGame.isEnabled = GameSession.gameState == GameSession.GameState.IDLE
        buttonPauseGame.isEnabled = GameSession.gameState == GameSession.GameState.COMPUTER_TURN ||
                GameSession.gameState == GameSession.GameState.PAUSED
        buttonEndOfGame.isEnabled = GameSession.gameState == GameSession.GameState.COMPUTER_TURN ||
                GameSession.gameState == GameSession.GameState.PAUSED ||
                GameSession.gameState == GameSession.GameState.PLAYER_TURN

        buttonPauseGame.text = if (GameSession.gameState == GameSession.GameState.PAUSED) {
            getString(R.string.resume_game)
        } else {
            getString(R.string.pause_game)
        }
    }
}