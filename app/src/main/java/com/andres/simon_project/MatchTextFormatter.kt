package com.andres.simon_project

/* useful to format match sequences colored in red starting from the errorIndex
* handled in this separate object file to keep the code as clean as possible */
object MatchTextFormatter {

    /* build the sequence with the new format */
    fun buildErrorSequenceFormattedText(match: GameSession.Match) {

    }

    /* find the index where the incorrect sequence starts */
    private fun getCharStartIndex(sequence: List<String>, targetIndex: Int) : Int {
        var idx = 0

        for (index in sequence.indices) {
            if (index == targetIndex) return idx // target index reached, where to start writing the sequence in red

            idx += sequence[index].length

            if (index < sequence.lastIndex) idx += 2
        }

        return idx
    }
}