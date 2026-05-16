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

        /* TODO: Will instantiate database or database Helper */
        dbHelper = MatchDBHelper(this)

        /* Bind variables with their UI elements (found by ID) */
        bindUIViews()

        /* Ensure instance state is preserved when orientation changes */
        handleInstanceState(savedInstanceState)

        /* Set interaction listeners for clickable colored TextViews and Buttons  */
        setInteractionListeners()

        /* Keep sequence updated after every click of the colored TextViews */
        printCurrentSequence()
    }

    /* called for example when the user from MatchListActivity pressed the back button and navigates back Home Screen */
    override fun onResume() {
        super.onResume()
        printCurrentSequence()
    }

    /* Overridden method for Instance State saving */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        /* TODO: will need to put states and data about the game */

        /* put the current sequence */
        outState.putStringArrayList(ID_CURRENT_SEQUENCE, ArrayList(GameSession.currentSequence))
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
        buttonEndOfGame = findViewById(R.id.buttonEndOfGame)
    }

    private fun handleInstanceState(savedInstanceState: Bundle?) {
        /* get the current sequence associated to the ID_CURRENT_SEQUENCE companion object */
        val handledSequence = savedInstanceState?.getStringArrayList(ID_CURRENT_SEQUENCE)

        /* if the sequence actually exists and is not NULL */
        if (handledSequence != null) {
            /* not calling the clearCurrentSequence() directly, because I want the class level currentSequence access
            * when dealing with instance state change */
            GameSession.currentSequence.clear()

            /* rewrite them in the sequence  */
            GameSession.currentSequence.addAll(handledSequence)
        }
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

        /* Buttons instead call only setOnClickListener */
        buttonStartGame.setOnClickListener {
            /* When Cancella is pressed, the TextView for sequence is cleared and
            removed from memory */
            GameSession.clearCurrentSequence() // now calling the clearCurrentSequence() method directly
            printCurrentSequence()
        }

        buttonEndOfGame.setOnClickListener {
            /* When Fine partita is pressed, TextView of sequence is cleared, sequence is saved and intent to MatchListActivity
            * is started */
            GameSession.endCurrentGame()
            /* was printing the Current sequence before, not useful though since the button EndOfGame
            * sends to the MatchListActivity */
            // printCurrentSequence()

            /* Start an intent to navigate to the MatchListActivity */
            val intent = Intent(this, MatchListActivity::class.java)
            startActivity(intent) // starts intent and navigates to the MatchListActivity
        }
    }
    private fun onColorClicked(colorName: String) {
        GameSession.appendColor(colorName) // add color to the sequence
        printCurrentSequence()
    }

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
}