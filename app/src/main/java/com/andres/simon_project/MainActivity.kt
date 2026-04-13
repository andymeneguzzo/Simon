package com.andres.simon_project

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    private lateinit var buttonCancel: Button
    private lateinit var buttonEndOfGame: Button

    /* Declare a companion object to identify the current sequence,
    useful for InstanceState preservation since it can be called at class level
    instead of instance level */
    companion object {
        private const val ID_CURRENT_SEQUENCE = "id_current_sequence"
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

        /* Bind variables with their UI elements (found by ID) */
        bindUIViews()

        /* Ensure instance state is preserved when orientation changes */
        handleInstanceState(savedInstanceState)

        /* Set interaction listeners for clickable colored TextViews and Buttons  */
        setInteractionListeners()

        /* Keep sequence updated after every click of the colored TextViews */
        printCurrentSequence()
    }

    /* Overridden method for Instance State handling */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        /* TODO: get sequence from the game session */
        outState.putStringArrayList(ID_CURRENT_SEQUENCE, ArrayList(/* TODO */))
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
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonEndOfGame = findViewById(R.id.buttonEndOfGame)
    }

    private fun handleInstanceState(savedInstanceState: Bundle?) {
        /* get the current sequence associated to the ID_CURRENT_SEQUENCE companion object */
        val handledSequence = savedInstanceState?.getStringArrayList(ID_CURRENT_SEQUENCE)

        /* if the sequence actually exists and is not NULL */
        if (handledSequence != null) {
            TODO("Handle in the session how the sequence is kept or restored")
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
        buttonCancel.setOnClickListener {
            /* When Cancella is pressed, the TextView for sequence is cleared and
            removed from memory */
            TODO("Handle in the session how game sequence is cleared from view and memory")
            printCurrentSequence()
        }

        buttonEndOfGame.setOnClickListener {
            TODO("Handle in session how game is ended")
            printCurrentSequence()

            /* Start an intent to navigate to the MatchListActivity */
            val intent = Intent(this, MatchListActivity::class.java)
            startActivity(intent)
        }
    }
    private fun onColorClicked(colorName: String) {
        TODO("handle in session how color is processed and appended to game sequence")
        printCurrentSequence()
    }

    private fun printCurrentSequence() {
        /* TODO: handle in game session the retrieval of sequence to print */
        val sequence = "Sequenza: ...";

        textViewSequence.text = sequence
    }
}