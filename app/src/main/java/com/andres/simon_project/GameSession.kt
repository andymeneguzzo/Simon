package com.andres.simon_project

/* object GameSession represents the information about a specific game session, specifically:
* - currentSequence: the sequence being played
* - matchHistory: the list of Match objects
* - methods to clear the played sequence, append a color to the sequence, end the current game */
object GameSession {

    /* Game states, so I can link logic to a state of the GameSession */
    enum class GameState {
        IDLE, // nothing's happening
        COMPUTER_TURN, // computer is showing sequence
        PAUSED, // game paused
        PLAYER_TURN, // player is inputting sequence
        GAME_OVER // game is ended
    }

    /* Model the result of the player clicking a color as a state as well */
    enum class PlayerClickResult {
        IGNORED, // player is not allowed yet to click
        PARTIALLY_CORRECT, // it is partly correct
        ROUND_COMPLETED, // round is over and correct
        WRONG // it's wrong
    }

    /* Match is a completed game, represented as a Data class, a class mainly to contain data.
    * Match in fact contains only the number of colored rectangles pressed */
    /* TODO: see if something needs to change here */
    data class Match(val sequence: List<String>) {
        val pressCount : Int
            /* get method returns the size of the sequence */
            get() = sequence.size
    }

    /* These are the colors the computer can choose from when giving out the sequence */
    private val availableColors = listOf("R", "G", "B", "M", "Y", "C")

    /* Sequence created by computer */
    val computerSequence: MutableList<String> = mutableListOf()

    /* currentSequence changes when user presses new colored TextViews, therefore
    * use a MutableList */
    /* this is the sequence pressed by the player during his turn */
    val currentSequence: MutableList<String> = mutableListOf()

    /* history changes as well, so use MutableList -> this will be loaded from SQLite db */
    val matchHistory: MutableList<Match> = mutableListOf()

    /* Note on why I chose MutableList<...>: this data structure allows to create a generic ordered collection of elements
    * and I can simply add or remove elements from it with add(...) and clear(...) */

    /* current game state */
    var gameState: GameState = GameState.IDLE // starts in IDLE always

    /* keep track of the index during the computer presentation of the sequence */
    var computerPresentationIndex: Int = 0

    /* max length of the correctly reproduced sequence */
    var maxCorrectLength: Int = 0


    /* When starting a new game, everything is set to default or initial value */
    fun startNewGame() {
        gameState = GameState.COMPUTER_TURN // starts with computer showing the sequence
        computerPresentationIndex = 0
        maxCorrectLength = 0
    }



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