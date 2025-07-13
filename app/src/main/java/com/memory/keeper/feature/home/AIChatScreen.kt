package com.memory.keeper.feature.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.MainActivity
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.util.MySpeechRecognizer
import com.memory.keeper.feature.util.PermissionDialog
import com.memory.keeper.feature.util.TTSManager
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun AIChatScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) } // 초기 선택 인덱스
    var steps by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    var recognizer by remember { mutableStateOf<MySpeechRecognizer?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    var isPaused by remember { mutableStateOf(false) }
    val ttsManager = remember { TTSManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(steps){
            0 -> {
                TopicChoice(
                    topics = listOf("일상", "취미", "가족", "친구", "학교", "기타"),
                    selectedIndex = selectedIndex,
                    onClickTopic = { index ->
                        selectedIndex = index
                    },
                    onClickBtn = {
                        steps = 2
                        //이미지 생성
                        recognizer = MySpeechRecognizer(
                            context = context,
                            onResult = { text ->
                                recognizedText = text
                                viewModel.startChat(text)
                            },
                            onError = { error ->
                                Log.e("Speech", "Error: $error")
                                Toast.makeText(context, "이해를 잘 하지 못하였습니다.", Toast.LENGTH_SHORT).show()
                            },
                            onFinished = {
                                Log.d("Speech", "10분 종료됨")
                                steps = 3
                            }
                        ).also {
                            it.start()
                        }
                    },
                    showDialog = showDialog
                )
            }
            1 -> {
                if(uiState == HomeUIState.Loading){
                    LoadingImageBox()
                }
            }
            2 -> {
                TalkingContent(
                    isLoading = uiState == HomeUIState.Loading,
                    image = "",
                    isPaused = isPaused,
                    onClickBtn1 = {
                        if(isPaused){
                            recognizer?.resume()
                            isPaused = false
                        }else{
                            recognizer?.pause()
                            isPaused = true
                        }
                    },
                    onClickBtn2 = {
                        recognizer?.stop()
                        ttsManager.shutdown()
                        steps = 3
                    },
                    recognizedText = recognizedText,
                )
            }
            3 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "대화가 종료되었습니다.",
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textPrimary
                    )
                }
            }
        }
        LaunchedEffect(aiResponse) {
            if(aiResponse != null){
                recognizer?.pause()
                ttsManager.speak(
                    text = aiResponse ?: "다시 한 번 말씀해주시겠어요?",
                    onDone = {recognizer?.resume()}
                )
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                recognizer?.stop()
                ttsManager.shutdown()
            }
        }
    }
    if(showDialog.value){
        PermissionDialog(
            showDialog = showDialog,
            message = "대화를 시작하려면 음성 권한이 필요합니다.",
            onDismiss = { showDialog.value = false },
            onConfirm = {
                showDialog.value = false
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
            }
        )
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when(event){
                is HomeUIEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is HomeUIEvent.ImageGenerated -> {
                    steps = 2
                    recognizer = MySpeechRecognizer(
                        context = context,
                        onResult = { text ->
                            Log.d("Speech", "인식 결과: $text")
                            viewModel.startChat(text)
                        },
                        onError = { error ->
                            Log.e("Speech", "Error: $error")
                            Toast.makeText(context, "이해를 잘 하지 못하였습니다.", Toast.LENGTH_SHORT).show()
                        },
                        onFinished = {
                            Log.d("Speech", "10분 종료됨")
                        }
                    ).also {
                        it.start()
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicChoice(
    topics: List<String>,
    selectedIndex: Int,
    onClickTopic: (Int) -> Unit,
    onClickBtn: () -> Unit,
    showDialog: MutableState<Boolean>
){
    val context = LocalContext.current
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("AIChatScreen", "Audio permission granted")
        } else {
            Log.d("AIChatScreen", "Audio permission denied")
        }
    }
    val recordAudioPermission = Manifest.permission.RECORD_AUDIO
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Text(
            text = "대화 주제를 선택해주세요",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ){
            for(i in 0..2){
                Card (
                    modifier = Modifier.padding(vertical = Dimens.gapMedium).size(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                        contentColor = if(selectedIndex == i) MemoryTheme.colors.optionTextFocused else MemoryTheme.colors.optionTextUnfocused
                    ),
                    shape = CircleShape,
                    onClick = {
                        onClickTopic(i)
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = topics[i],
                            style = MemoryTheme.typography.option,
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            for(i in 3..5){
                Card (
                    modifier = Modifier.padding(vertical = Dimens.gapMedium).size(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                        contentColor = if(selectedIndex == i) MemoryTheme.colors.optionTextFocused else MemoryTheme.colors.optionTextUnfocused
                    ),
                    shape = CircleShape,
                    onClick = {
                        onClickTopic(i)
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = topics[i],
                            style = MemoryTheme.typography.option,
                        )
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.gapLarge),
            shape = RoundedCornerShape(Dimens.cornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = MemoryTheme.colors.primary,
                disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
                contentColor = MemoryTheme.colors.buttonText,
                disabledContentColor = MemoryTheme.colors.buttonText
            ),
            onClick = {
                when {
                    ContextCompat.checkSelfPermission(context, recordAudioPermission) == PackageManager.PERMISSION_GRANTED -> {
                        onClickBtn()
                    }
                    shouldShowRequestPermissionRationale(context as MainActivity,
                        recordAudioPermission) -> {
                        showDialog.value = true
                    }
                    else -> {
                        audioPermissionLauncher.launch(recordAudioPermission)
                    }
                }
            },
            enabled = selectedIndex != -1
        ) {
            Text(
                text = "대화 시작하기",
                style = MemoryTheme.typography.button,
            )
        }
    }
}

@Composable
private fun LoadingImageBox(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "이미지 생성 중...",
            style = MemoryTheme.typography.body,
            color = MemoryTheme.colors.textPrimary
        )
        Spacer(
            modifier = Modifier.size(Dimens.gapLarge)
        )
        CircularProgressIndicator(
            color = MemoryTheme.colors.primary
        )
    }
}

@Composable
private fun TalkingContent(
    isLoading : Boolean,
    image: String,
    isPaused: Boolean = false,
    onClickBtn1: () -> Unit = {},
    onClickBtn2: () -> Unit = {},
    recognizedText: String,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
//        AsyncImage(
//            model = image,
//            contentDescription = "Generated Image",
//            modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
//            contentScale = ContentScale.Crop
//        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.voice),
            contentDescription = "Voice Icon",
            tint = Color.Unspecified,
        )
        if(isLoading){
            Text(
                text = "AI가 응답을 준비 중입니다...",
                style = MemoryTheme.typography.body,
                color = MemoryTheme.colors.textPrimary
            )
        }else{
            Text(
                text = recognizedText,
                style = MemoryTheme.typography.body,
                color = MemoryTheme.colors.textPrimary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MemoryTheme.colors.surface,
                    disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
                    contentColor = MemoryTheme.colors.textPrimary,
                    disabledContentColor = MemoryTheme.colors.buttonText
                ),
                border = BorderStroke(width = 3.dp, color = MemoryTheme.colors.primary),
                onClick = onClickBtn1,
            ) {
                Text(
                    text = if(isPaused) "재개하기" else "중단하기",
                    style = MemoryTheme.typography.button,
                )
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MemoryTheme.colors.surface,
                    disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
                    contentColor = MemoryTheme.colors.textPrimary,
                    disabledContentColor = MemoryTheme.colors.buttonText
                ),
                border = BorderStroke(width = 3.dp, color = MemoryTheme.colors.red),
                onClick = onClickBtn2,
            ) {
                Text(
                    text = "종료하기",
                    style = MemoryTheme.typography.button,
                )
            }
        }
    }
}
