package com.memory.keeper.feature.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.KOREAN)
            isReady = result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE
        } else {
            Log.e("TTS", "초기화 실패")
        }
    }

    fun speak(text: String, onDone: () -> Unit = {}) {
        if (!isReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID")

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                onDone()
            }

            override fun onError(utteranceId: String?) {}
            override fun onStart(utteranceId: String?) {}
        })
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}