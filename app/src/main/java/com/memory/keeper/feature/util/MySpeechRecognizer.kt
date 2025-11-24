package com.memory.keeper.feature.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale


class MySpeechRecognizer(
    context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onFinished: () -> Unit,
) {
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val handler = Handler(Looper.getMainLooper())

    private var isListening = false
    private var isPaused = false
    private val totalTimeLimit = 10 * 60 * 1000L // 10분
    private var startTime = 0L

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!text.isNullOrBlank()) onResult(text)
                restartListeningIfNeeded()
            }

            override fun onError(error: Int) {
                if (error != SpeechRecognizer.ERROR_CLIENT &&
                    error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
                ) {
                    restartListeningIfNeeded()
                } else {
                    stop()
                    onError("오류 발생: $error")
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun start() {
        if (isListening) return
        Log.d("Speech", "started")
        startTime = System.currentTimeMillis()
        isListening = true
        isPaused = false
        listen()

        handler.postDelayed({
            stop()
            onFinished()
        }, totalTimeLimit)
    }

    fun stop() {
        Log.d("Speech", "stopped")
        isListening = false
        recognizer.stopListening()
        recognizer.cancel()
        recognizer.destroy()
    }

    fun pause() {
        Log.d("Speech", "paused")
        isPaused = true
        isListening = false
        recognizer.stopListening()
    }

    fun resume() {
        if (!isPaused || !isListening) return
        Log.d("Speech", "resumed")
        isPaused = false
        listen()
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        recognizer.startListening(intent)
    }

    private fun restartListeningIfNeeded() {
        if (isListening && !isPaused && System.currentTimeMillis() - startTime < totalTimeLimit) {
            handler.postDelayed({ listen() }, 300)
        }
    }
}