package com.andres.simon_project

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Here we show a single match with more details than the RecyclerView
 * User can exit using the system back
 */
class MatchDetailActivity : AppCompatActivity() {

    // need ID of the match
    companion object {
        const val MATCH_ID = "match_id"
    }

    // DB
    private lateinit var dbHelper: MatchDBHelper

    // UI
    private lateinit var textViewPressCount: TextView
    private lateinit var textViewSequence: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_match_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.matchDetail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = MatchDBHelper(this)
        // todo -> bind UI views, render the detail
    }
    private fun bindUIViews() {
        textViewPressCount = findViewById(R.id.textViewPressCount)
        textViewSequence = findViewById(R.id.textViewSequence)
    }
    private fun printMatchDetail() {

    }
}