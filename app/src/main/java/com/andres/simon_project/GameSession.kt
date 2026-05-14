package com.andres.simon_project

import kotlin.random.Random

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
    enum class PlayerPressResult {
        IGNORED, // player is not allowed yet to click
        PARTIALLY_CORRECT, // it is partly correct
        ROUND_COMPLETED, // round is over and correct
        WRONG // it's wrong
    }

    /* Match is a completed game, represented as a Data class, a class mainly to contain data.
    * Match in fact contains only the number of colored rectangles pressed */
    /**
    data class Match(val sequence: List<String>) {
        val pressCount : Int
            /* get method returns the size of the sequence */
            get() = sequence.size
    }
    */
    data class Match(
        val id: Long = 0L, // as a good practice and will be useful for SQLite
        val maxCorrectLength: Int, // as required, the max correct sequence length
        val errorSequence: List<String>, // the sequence after the error
        val errorIndex: Int,
        val createdAt: Long = System.currentTimeMillis() // to make the "from most recent" ordering more robust, every match will have a createdAt timestamp
    )

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

    /* as a case, I consider the possibility of a game finished but not yet saved
    * so add a Match object with the game info still waiting to be saved */
    var matchYetToBeSaved: Match? = null


    /* When starting a new game, everything is set to default or initial value */
    fun startNewGame() {
        clearGameState()

        generateRandomColor()

        gameState = GameState.COMPUTER_TURN // starts with computer showing the sequence
    }
    /* clears variables for a new game start */
    fun clearGameState() {
        computerSequence.clear()
        currentSequence.clear()
        gameState = GameState.IDLE // back to IDLE, then set to COMPUTER_TURN when the game started
        computerPresentationIndex = 0
        maxCorrectLength = 0
        matchYetToBeSaved = null // no match to save
    }
    /* used by computer to append a random color to the sequence */
    fun generateRandomColor() {
        val randomIndex = Random.nextInt(availableColors.size)
        computerSequence.add(availableColors[randomIndex])
    }


    /* after computer turn, method is called when it's player turn */
    fun startPlayerTurn() {
        currentSequence.clear() // to make sure my sequence is cleared
        computerPresentationIndex = 0 // now it's my turn
        gameState = GameState.PLAYER_TURN
    }
    /* handle the color pressed by the player */
    fun handlePlayerColorPressed(color: String) : PlayerPressResult {
        if (gameState != GameState.PLAYER_TURN) return PlayerPressResult.IGNORED // player not allowed to input a color

        val pressedIndex = currentSequence.size // my position in sequence of what I pressed
        val expectedColor = computerSequence.getOrNull(pressedIndex) // the color in computer sequence at the same position
        currentSequence.add(color)

        if (expectedColor != color) {
            finishGameAfterError(pressedIndex) // pass the index in which error occured
            return PlayerPressResult.WRONG
        }

        if (currentSequence.size == computerSequence.size) {
            // means the two sequences are same length, so the round is over
            maxCorrectLength = computerSequence.size
            currentSequence.clear()
            generateRandomColor() // now it's going to be computer turn

            gameState = GameState.COMPUTER_TURN
            computerPresentationIndex = 0

            return PlayerPressResult.ROUND_COMPLETED
        }

        // if not wrong, then
        return PlayerPressResult.PARTIALLY_CORRECT // because round still going
    }
    fun finishGameAfterError(errorPosition: Int) {
        if (computerSequence.isEmpty()) {
            // no computer sequence, then just clear the state and end method call
            clearGameState()
            return
        }

        val safeErrorPosition = errorPosition.coerceIn(0, computerSequence.lastIndex) // can use .lastIndex since computerSequence is a MutableList
        matchYetToBeSaved = Match(
            maxCorrectLength = maxCorrectLength,
            errorSequence = computerSequence.toList(),
            errorIndex = safeErrorPosition
        )

        gameState = GameState.GAME_OVER
    }
    /* called when the user the endOfGame button or uses the system Back while the game is still running */
    fun finishGameAfterInterrupt() {
        if (computerSequence.isEmpty()) {
            // nothing really happens
            clearGameState()
            return
        }

        // get the position in which we stopped
        val interruptIndex = when (gameState) {
            GameState.PLAYER_TURN -> currentSequence.size
            GameState.COMPUTER_TURN -> computerPresentationIndex
            GameState.PAUSED -> computerPresentationIndex // when paused during computer generation
            else -> 0 // nothing happened yet
        }.coerceIn(0, computerSequence.lastIndex) // keep it between 0 and the computerSequence last index

        // end the game passing the index of interrupt
        finishGameAfterError(interruptIndex)
    }

    /* As requested, when the user stops the computer proposal at length 1, the game is just discarded, no saved and the match is not saved */
    fun isComputerPresentationDiscardable() : Boolean {
        return computerSequence.size == 1 && // computer presented a length 1 sequence
                maxCorrectLength == 0 && // user got nothing right
                currentSequence.isEmpty() && // user inserted nothing
                (gameState == GameState.COMPUTER_TURN || gameState == GameState.PAUSED) // game was paused or it was computer's turn
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
        // matchHistory.add(0, Match(finalSequence)) // add to top for "most recent" ordering
        currentSequence.clear()
    }
}