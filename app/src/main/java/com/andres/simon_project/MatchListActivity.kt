package com.andres.simon_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/* In this Activity the user can visualize the list of matches played in order starting from most recent matches.
*  Thanks to MatchAdapter class, the RecyclerView will be updated with new matches as the user completes the matches
*  and will list the match items with layout inflated in the MatchAdapter class */
class MatchListActivity : AppCompatActivity() {

    /* Back button */
    // private lateinit var buttonBack: ImageButton -> will be using the system back instead of the button
    // todo: change also UI file to remove the back button

    /* MatchAdapter and the dynamic list of matches */
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var dynamicListGames: RecyclerView
    private lateinit var buttonNewGame: FloatingActionButton // newly added button

    // Database
    private lateinit var dbHelper: MatchDBHelper


    /* onCreate() method called at launch of Activity */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_match_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.matchList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // instantiate db
        dbHelper = MatchDBHelper(this)

        /* Bind the variables with their UI element */
        bindUIViews()

        /* set up the RecyclerView for the dynamic list of matches */
        setupRecyclerViewMatches()

        /* set up the interaction listeners */
        setupInteractionListeners()
    }
    private fun loadMatchesFromDB() {

    }

    override fun onResume() {
        super.onResume()
        // todo: need to load the matches from db
    }


    private fun bindUIViews() {
        // buttonBack = findViewById(R.id.buttonBack)
        dynamicListGames = findViewById(R.id.dynamicListGames)
        buttonNewGame = findViewById(R.id.buttonNewGame)
    }

    private fun setupRecyclerViewMatches() {
        /* create a MatchAdapter for match history */
        matchAdapter = MatchAdapter(GameSession.matchHistory) {
            val intent = Intent(this, MatchDetailActivity::class.java)
            // todo: will need to send the ID of the match I want to see the details of
            startActivity(intent)
        }

        /* the RecyclerView shows the matches elements as a vertical list, so I use the LayoutManager */
        dynamicListGames.layoutManager = LinearLayoutManager(this)
        dynamicListGames.adapter = matchAdapter
    }

    private fun setupInteractionListeners() {
        buttonBack.setOnClickListener {
            /* ensure the sequence is cleared so user can play a new one */
            GameSession.clearCurrentSequence()

            // activity is done and should be closed
            finish()
        }
    }
}