package com.andres.simon_project

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

/* useful to format match sequences colored in red starting from the errorIndex
* handled in this separate object file to keep the code as clean as possible */
object MatchTextFormatter {

    /* build the sequence with the new format */
    /* will return a Spannable string -> allows to create dynamic strings without recreating TextViews, but modifying its color "in place" */
    fun buildErrorSequenceFormattedText(match: GameSession.Match) : SpannableString {
        val sequenceText = match.errorSequence.joinToString(", ") // sequence text joined
        val spannable = SpannableString(sequenceText)

        if (match.errorSequence.isEmpty()) return spannable

        val safeErrorIdx = match.errorIndex.coerceIn(0, match.errorSequence.lastIndex) // here I take the error index and coerce it to ensure it's meaningful
        val spanStart = getCharStartIndex(match.errorSequence, safeErrorIdx) // get the actual index where to start the span (red coloring)

        spannable.setSpan(
            ForegroundColorSpan(Color.RED), // set text to red color
            spanStart, // start from spanStart
            sequenceText.length, // go up to the end of the sequence
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // means that in case text was appended at the end of sequence, it will not be colored red (won't be part of the span)
        )

        // return the actual spanned string
        return spannable
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