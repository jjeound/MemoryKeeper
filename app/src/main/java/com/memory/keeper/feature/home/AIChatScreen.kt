package com.memory.keeper.feature.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.memory.keeper.MainActivity
import com.memory.keeper.ui.theme.MemoryTheme
import java.util.Locale

@Composable
fun AIChatScreen() {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("여기에 음성 결과가 표시됩니다") }
    var showDialog by remember { mutableStateOf(false) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startSpeechRecognition(context) { text ->
                recognizedText = text
            }
        } else {
            recognizedText = "음성 권한이 거부되었습니다."
        }
    }
    val recordAudioPermission = Manifest.permission.RECORD_AUDIO

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = recognizedText,
            style = MemoryTheme.typography.body,
            color = MemoryTheme.colors.textPrimary,
        )
        Button(
            onClick = {
                when {
                    ContextCompat.checkSelfPermission(context, recordAudioPermission) == PackageManager.PERMISSION_GRANTED -> {
                        startSpeechRecognition(context) { text ->
                            recognizedText = text
                        }
                    }
                    shouldShowRequestPermissionRationale(context as MainActivity,
                        recordAudioPermission) -> {
                        showDialog = true
                    }
                    else -> {
                        audioPermissionLauncher.launch(recordAudioPermission)
                    }
                }
            }
        ) {
            Text(
                text = "대화하기",
                style = MemoryTheme.typography.button,
                color = MemoryTheme.colors.textPrimary,
            )
        }
    }
}

fun startSpeechRecognition(context: Context, onResult: (String) -> Unit) {
    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onResult(matches?.firstOrNull() ?: "인식 실패")
            recognizer.destroy()
        }

        override fun onError(error: Int) {
            onResult("오류 발생: $error")
            recognizer.destroy()
        }

        // 생략 가능
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    recognizer.startListening(intent)
}
