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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.memory.keeper.MainActivity
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.login.SignUpBottomButton
import com.memory.keeper.feature.util.MySpeechRecognizer
import com.memory.keeper.feature.util.PermissionDialog
import com.memory.keeper.feature.util.TTSManager
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.delay

@Composable
fun AIChatScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) } // 초기 선택 인덱스
    var steps by rememberSaveable { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    var recognizer by remember { mutableStateOf<MySpeechRecognizer?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    val ttsManager = remember { TTSManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(steps){
            0 -> {
                TopicChoice(
                    topics = listOf(
                        R.drawable.image_option1, R.drawable.image_option2,
                        R.drawable.image_option3, R.drawable.image_option4,
                        R.drawable.image_option5, R.drawable.image_option6
                    ),
                    selectedIndex = selectedIndex,
                    onClickTopic = { index ->
                        selectedIndex = index
                    },
                    onClickBtn = {
                        steps = 1
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
                                steps = 2
                            }
                        ).also {
                            it.start()
                        }
                    },
                    showDialog = showDialog
                )
            }
            1 -> {
                TalkingContent(
                    isLoading = uiState == HomeUIState.Loading,
                    image = R.drawable.image_option2_big,
                    onClick = {
                        recognizer?.stop()
                        ttsManager.shutdown()
                        steps = 2
                    },
                    recognizedText = recognizedText,
                    aiResponse = aiResponse
                )
            }
            2 -> {
                ImageDialog(
                    onConfirm = {
                        //viewModel.generateImage
                        steps = 3
                    },
                    onDismiss = {
                        steps = 7
                    }
                )
            }
            3 -> {
                if(uiState == HomeUIState.Loading){
                    LoadingImageBox(
                        title = "이미지 생성 중..",
                    )
                }
            }
            4 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
                    ){
                        AsyncImage(
                            model = R.drawable.ai_image,
                            contentDescription = "AI Generated Image",
                        )
                        SignUpBottomButton(
                            enabled = true,
                            onClick = {steps = 5},
                            title = "영상 생성하기"
                        )
                    }
                }
            }
            5 -> {
                if(uiState == HomeUIState.Loading){
                    LoadingImageBox(
                        title = "영상 생성 중...",
                    )
                }
            }
            6 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(
                        contentAlignment = Alignment.Center
                    ){
                        AsyncImage(
                            model = R.drawable.ai_image,
                            contentDescription = "AI Generated Image",
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.play),
                            contentDescription = "play",
                            tint = Color.Unspecified
                        )
                    }
                    Text(
                        text = "대화가 종료되었습니다.",
                        style = MemoryTheme.typography.header,
                        color = MemoryTheme.colors.textPrimary
                    )
                }
            }
            7 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "대화가 종료되었습니다.",
                        style = MemoryTheme.typography.header,
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
    topics: List<Int>,
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            for(i in 0..1){
                Box(
                    modifier = Modifier.clickable{
                        onClickTopic(i)
                    }
                ) {
                    AsyncImage(
                        modifier = Modifier.size(width = 150.dp, height = 120.dp).border(
                            if(selectedIndex == i) 3.dp else 0.dp,
                            color = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                            shape = RoundedCornerShape(Dimens.cornerRadius)
                        ),
                        model = topics[i],
                        contentDescription = "image option",
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            for(i in 2..3){
                Box(
                    modifier = Modifier.clickable{
                        onClickTopic(i)
                    }
                ) {
                    AsyncImage(
                        modifier = Modifier.size(width = 150.dp, height = 120.dp).border(
                            if(selectedIndex == i) 3.dp else 0.dp,
                            color = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                            shape = RoundedCornerShape(Dimens.cornerRadius)
                        ),
                        model = topics[i],
                        contentDescription = "image option",
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            for(i in 4..5){
                Box(
                    modifier = Modifier.clickable{
                        onClickTopic(i)
                    }
                ) {
                    AsyncImage(
                        modifier = Modifier.size(width = 150.dp, height = 120.dp).border(
                            if(selectedIndex == i) 3.dp else 0.dp,
                            color = if(selectedIndex == i) MemoryTheme.colors.primary else MemoryTheme.colors.optionUnfocused,
                            shape = RoundedCornerShape(Dimens.cornerRadius)
                        ),
                        model = topics[i],
                        contentDescription = "image option",
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

@Composable
private fun TalkingContent(
    isLoading : Boolean,
    image: Int,
    onClick: () -> Unit = {},
    recognizedText: String,
    aiResponse: String?
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        AsyncImage(
            model = image,
            contentDescription = "AI Image",
            modifier = Modifier.fillMaxWidth()
        )
        ElevatedCard (
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MemoryTheme.colors.surface,
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = Dimens.gapSmall
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Dimens.gapLarge),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = recognizedText,
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary,
                    softWrap = true
                )
                Spacer(
                    modifier = Modifier.width(Dimens.gapMedium)
                )
                AsyncImage(
                    model = R.drawable.persona,
                    contentDescription = "logo",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        aiResponse?.let {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MemoryTheme.colors.optionFocused,
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = Dimens.gapSmall
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Dimens.gapLarge),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = R.drawable.logo,
                        contentDescription = "logo",
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = it,
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textPrimary,
                        softWrap = true
                    )
                }
            }
        }
        if(isLoading){
            Text(
                text = "AI가 응답을 준비 중입니다...",
                style = MemoryTheme.typography.body,
                color = MemoryTheme.colors.textPrimary
            )
        }
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = ImageVector.vectorResource(R.drawable.voice),
            contentDescription = "Voice Icon",
            tint = Color.Unspecified,
        )
        SignUpBottomButton(
            enabled = true,
            onClick = onClick,
            title = "대화 종료하기"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
){
    BasicAlertDialog(
        modifier = Modifier.background(MemoryTheme.colors.surface),
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.wrapContentHeight(),
            shape = RoundedCornerShape(Dimens.boxCornerRadius)
        ){
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
                        onClick = onConfirm,
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
}
