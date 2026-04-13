package com.andres.simon_project

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/* In this Activity the user can visualize the list of matches played in order starting from most recent matches.
*  Thanks to MatchAdapter class, the RecyclerView will be updated with new matches as the user completes the matches
*  and will list the match items with layout inflated in the MatchAdapter class */
class MatchListActivity : AppCompatActivity() {

    /* Back button */
    private lateinit var buttonBack: ImageButton

    /* MatchAdapter and the dynamic list of matches */
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var dynamicListGames: RecyclerView

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

        /* Bind the variables with their UI element */
        bindUIViews()

        /* setup the RecyclerView for the dynamic list of matches */
        setupRecyclerViewMatches()

        /* setup the interaction listeners */
        setupInteractionListeners()
    }


    private fun bindUIViews() {
        buttonBack = findViewById(R.id.buttonBack)
        dynamicListGames = findViewById(R.id.dynamicListGames)
    }

    private fun setupRecyclerViewMatches() {
        /* create a MatchAdapter for match history */
        matchAdapter = MatchAdapter(GameSession.matchHistory)

        /* the RecyclerView shows the matches elements as a vertical list, so I use the LayoutManager */
        dynamicListGames.layoutManager = LinearLayoutManager(this)
        dynamicListGames.adapter = matchAdapter
    }

    private fun setupInteractionListeners() {
        buttonBack.setOnClickListener {
            /* ensure the sequence is cleared so user can play a new one */
            GameSession.clearCurrentSequence()

            finish()
        }
    }
}