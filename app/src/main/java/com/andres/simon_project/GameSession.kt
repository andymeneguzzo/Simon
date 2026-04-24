package com.andres.simon_project

/* object GameSession represents the information about a specific game session, specifically:
* - currentSequence: the sequence being played
* - matchHistory: the list of Match objects
* - methods to clear the played sequence, append a color to the sequence, end the current game */
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

    /* Note on MutableList<...>: this data structure allows to create a generic ordered collection of elements
    * and I can simply add or remove elements from it with add(...) and clear(...) */

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

        /* when game is ended, the sequence is added to matchHistory */
        matchHistory.add(0, Match(finalSequence)) // add to top for "most recent" ordering
        currentSequence.clear()
    }
}