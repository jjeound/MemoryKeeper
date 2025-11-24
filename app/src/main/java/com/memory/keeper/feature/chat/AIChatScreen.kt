package com.memory.keeper.feature.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.memory.keeper.MainActivity
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.response.Topic
import com.memory.keeper.feature.util.MySpeechRecognizer
import com.memory.keeper.feature.util.PermissionDialog
import com.memory.keeper.feature.util.TTSManager
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun AIChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) } // 초기 선택 인덱스
    val steps by viewModel.steps
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    var recognizer by remember { mutableStateOf<MySpeechRecognizer?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    val ttsManager = remember { TTSManager(context) }
    val images by viewModel.aiImage.collectAsStateWithLifecycle()
    val composeNavigator = currentComposeNavigator

    LaunchedEffect(aiResponse) {
        if(!aiResponse.isNullOrBlank()){
            ttsManager.speak(
                text = aiResponse ?: "다시 한 번 말씀해주시겠어요?",
                onDone = {
                    recognizer?.resume()
                    isListening = true
                }
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            recognizer?.stop()
            ttsManager.shutdown()
        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when(event){
                is ChatUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is ChatUiEvent.ImageGenerated -> {
                    viewModel.setSteps(4)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(steps){
            0 -> {
                if(topics.isNotEmpty() && uiState != ChatUiState.Loading) {
                    TopicChoice(
                        topics = topics,
                        selectedIndex = selectedIndex,
                        onClickTopic = { index ->
                            selectedIndex = index
                        },
                        onClickBtn = {
                            viewModel.startChat(
                                "", topic = topics[selectedIndex].topic
                            )
                            viewModel.setSteps(1)
                            recognizer = MySpeechRecognizer(
                                context = context,
                                onResult = { text ->
                                    recognizedText = text
                                    viewModel.startChat(text, topics[selectedIndex].topic)
                                    isListening = false
                                    recognizer?.pause()
                                },
                                onError = { error ->
                                    Log.e("Speech", "Error: $error")
                                    Toast.makeText(context, "이해를 잘 하지 못하였습니다.", Toast.LENGTH_SHORT).show()
                                },
                                onFinished = {
                                    Log.d("Speech", "10분 종료됨")
                                    viewModel.setSteps(2)
                                }
                            ).also {
                                it.start()
                            }
                        },
                        showDialog = showDialog
                    )
                } else {
                    LoadingImageBox(title = "주제를 불러오는 중입니다...")
                }
            }
            1 -> {
                ChattingScreen(
                    isListening = isListening,
                    onClick = {
                        recognizer?.stop()
                        ttsManager.shutdown()
                        viewModel.setSteps(2)
                    },
                    title = "대화 종료하기",
                    uiState = uiState
                )
            }
            2 -> {
                ImageCreateDialog(
                    onConfirm = {
                        viewModel.saveConversation(true, selectedIndex)
                        viewModel.setSteps(3)
                    },
                    onDismiss = {
                        viewModel.saveConversation(false, 0)
                        composeNavigator.navigate(Screen.Chat){
                            popUpTo(0) { inclusive = true } // 모든 백스택 제거
                            launchSingleTop = true
                        }
                    }
                )
            }
            3 -> {
                LoadingImageBox(title = "이미지를 생성중 입니다...\n평균 1분 정도 소요됩니다")
            }
            4 -> {
                images.forEach {
                    AsyncImage(
                        model = it,
                        contentDescription = null
                    )
                }
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                Text(
                    text = "대화가 종료되었습니다.",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary
                )
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
}

@Composable
private fun TopicChoice(
    topics: List<Topic>,
    selectedIndex: Int,
    onClickTopic: (Int) -> Unit,
    onClickBtn: () -> Unit,
    showDialog: MutableState<Boolean>
){
    Log.d("topics", topics.toString())
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            for(i in 0..1){
                Column (
                    modifier = Modifier.clickable{
                        onClickTopic(i)
                    },
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.size(width = 150.dp, height = 120.dp).border(
                            if(selectedIndex == i) 3.dp else 0.dp,
                            color = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                        ),
                        model = topics[i].url,
                        contentScale = ContentScale.Crop,
                        contentDescription = "image option",
                    )
                    Text(
                        text = topics[i].topic.replace(".jpg", ""),
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textPrimary
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            for(i in 2..3){
                Column (
                    modifier = Modifier.clickable{
                        onClickTopic(i)
                    },
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.size(width = 150.dp, height = 120.dp).border(
                            if(selectedIndex == i) 3.dp else 0.dp,
                            color = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                        ),
                        contentScale = ContentScale.Crop ,
                        model = topics[i].url,
                        contentDescription = "image option",
                    )
                    Text(
                        text = topics[i].topic.replace(".jpg", ""),
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textPrimary
                    )
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
private fun LoadingImageBox(
    title: String,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCreateDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
){
    BasicAlertDialog(
        modifier = Modifier.background(
            shape = RoundedCornerShape(12.dp),
            color = MemoryTheme.colors.surface
        ),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(
                30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapHuge)
        ) {
            Text(
                text = "대화가 종료되었어요.\n" +
                        "이미지를 생성하시겠어요?",
                style = MemoryTheme.typography.headlineLarge,
                color = MemoryTheme.colors.textPrimary
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.primary,
                        contentColor = MemoryTheme.colors.buttonText
                    )
                ) {
                    Text(
                        text = "생성하기",
                        style = MemoryTheme.typography.button,
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.optionUnfocused,
                        contentColor = MemoryTheme.colors.textPrimary
                    )
                ) {
                    Text(
                        text = "다음에 하기",
                        style = MemoryTheme.typography.button,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ImageDialogPreview(){
    MemoryTheme {
        ImageCreateDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}