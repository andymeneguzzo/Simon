package com.andres.simon_project

/* This allows to save the sequence and game state in the Session (until app termination)*/
object GameSession {

    /* Match is a data class containing pressCount */
    data class Match(val sequence: List<String>) {
        val pressCount : Int
            /* get method returns the size of the sequence */
            get() = sequence.size
    }

    /* currentSequence changes when user presses new colored TextViews, therefore
    * use a MutableList */
    val currentSequence: MutableList<String> = mutableListOf()

    /* history changes as well, so use MutableList */
    val matchHistory: MutableList<Match> = mutableListOf()

    /* Clear the sequence of colors */
    fun clearCurrentSequence() {
        /* currentSequence is a MutableList, so it's sufficient to call clear() method */
        currentSequence.clear()
    }

    /* Add a new color to the sequence */
    fun appendColor(colorName: String) {
        /* currentSequence as a MutableList can call add(...) method to append a new color to the list */
        currentSequence.add(colorName)
    }

    /* Terminate the current game */
    fun endCurrentGame() {
        /* save the sequence before termination, add to history then clear */
        val finalSequence = currentSequence.toList()
        matchHistory.add(0, Match(finalSequence)) // add to top for "most recent" ordering
        currentSequence.clear()
    }
}