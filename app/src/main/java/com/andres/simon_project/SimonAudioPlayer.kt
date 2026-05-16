package com.andres.simon_project

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

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

    /* function to play audio (accounting for thread safety), different one for each color */
    private fun playAudio(frequency: Double) {
        // how many samples needed
        val sampleNumber = SAMPLE_RATE * DURATION_MS / 1000

        // sample array (audio buffer)
        val samples = ShortArray(sampleNumber)

        // looping through the indices of the buffer
        for (i in samples.indices) {
            // compute the angle to make a sine wav for sample at the i-th position
            val angle = 2.0 * PI * i * frequency / SAMPLE_RATE
            // store the actual audio for the sample at i-th position
            samples[i] = (sin(angle) * Short.MAX_VALUE * VOLUME).toInt().toShort() // cast all the way from Double, to Int, to Short
        }

        val audioTrack = AudioTrack.Builder()
            // audio attributes -> usage, content type
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            // audio format -> encoding, sample rate, mono output
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            // buffer size -> size of samples * 2
            .setBufferSizeInBytes(samples.size * 2) // number of samples * 2 bytes per sample
            .setTransferMode(AudioTrack.MODE_STATIC) // this loads the whole audio in memory at once, then plays it
            .build() // build the actual AudioTrack object

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        // play without killing the thread too early
        Thread.sleep(DURATION_MS.toLong() + 20L)
        audioTrack.stop() // stop playing
        audioTrack.release() // release from memory
    }
    fun playAudioForColor(color: String) {
        /* assign to each color its frequency and play its audio */
        val freq = assignFrequencyToColor(color)
        Thread {
            playAudio(freq) // passed the frequency so the AudioTrack object is created for the specific color freq
        }.start()
    }
}