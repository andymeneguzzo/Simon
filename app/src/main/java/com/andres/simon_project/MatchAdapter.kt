package com.andres.simon_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/* This is useful and essential to handle item_game element which represents
* how the game will be shown in the MatchListActivity, and the RecyclerView that will
* contain the list of played games, so then the item_game can be inflated in the RecyclerView */
class MatchAdapter(private val matchItems: List<GameSession.Match>, onMatchClicked: (GameSession.Match) -> Unit) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    /* when ViewHolder created, inflate the item_game in the RecyclerView */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return MatchViewHolder(view)
    }

    /* When binding the ViewHolder, get the info from the matchItem of the list of matches, convert to String
    * and bind it to the TextViews of the item_game */
    override fun onBindViewHolder(
        holder: MatchViewHolder,
        position: Int
    ) {
        val matchItem = matchItems[position]

        holder.textViewPressCount.text = matchItem.maxCorrectLength.toString()

        /* sequence text gets text from the MatchTextFormatter */
        holder.textViewSequence.text = MatchTextFormatter.buildErrorSequenceFormattedText(matchItem)

        holder.itemView.setOnClickListener {
            /* todo: method to handle when match is clicked */
        }
    }

    /* only returns the size of matchItems list, so how many matches were played in the game session
    * currently never used but must be overridden */
    override fun getItemCount(): Int = matchItems.size

    /* This ViewHolder class holds and binds the elements of item_game */
    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPressCount: TextView = itemView.findViewById(R.id.textViewPressCount)
        val textViewSequence: TextView = itemView.findViewById(R.id.textViewSequence)
    }
}