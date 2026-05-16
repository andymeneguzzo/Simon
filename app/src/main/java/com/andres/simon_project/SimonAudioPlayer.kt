package com.andres.simon_project

/* This class uses AudioTrack to play a sound for each color */
class SimonAudioPlayer {

    /* set some constant params - take into account if constants are class-wise or object-wise */
    /* using a companion object, the constants will be linked to the class SimonAudioPlayer, not to its object */
    companion object {
        private const val SAMPLE_RATE = 44100 // a standard sample rate
        private const val DURATION_MS = 200 // 200ms of duration
        private const val VOLUME = 0.5 // volume of 50%
    }

    /* assign a frequency for each color */
    private fun assignFrequencyToColor(color: String) : Double {
        return when (color) {
            "R" -> 260.00
            "G" -> 330.00
            "B" -> 392.00
            "M" -> 494.00
            "Y" -> 587.00
            "C" -> 659.00
            else -> 440.00
        }
    }

    /* function to play audio (accounting for thread safety) */
}