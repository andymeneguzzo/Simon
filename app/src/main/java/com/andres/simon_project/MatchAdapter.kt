package com.andres.simon_project

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/* This is useful and essential to handle item_game element which represents
* how the game will be shown in the MatchListActivity, and the RecyclerView that will
* contain the list of played games, so then the item_game can be inflated in the RecyclerView */
class MatchAdapter(private val items: List<GameSession.Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MatchViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: MatchViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    /* This ViewHolder class holds and binds the elements in item_game */
    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPressCount: TextView = itemView.findViewById(R.id.textViewPressCount)
        val textViewSequence: TextView = itemView.findViewById(R.id.textViewSequence)
    }
}