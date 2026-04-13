package com.andres.simon_project

import android.os.Bundle
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
        TODO("handle Instance State preservation")
    }

    private fun setInteractionListeners() {
        TODO("Set all listeners for clickable TextViews and Buttons")
    }

    private fun printCurrentSequence() {
        TODO("Enable sequence update after every click of the colored TextViews")
    }

}