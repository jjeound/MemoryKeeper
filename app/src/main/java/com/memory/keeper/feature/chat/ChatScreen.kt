package com.memory.keeper.feature.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.memory.keeper.MainActivity
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.response.Topic
import com.memory.keeper.feature.home.HomeUIEvent
import com.memory.keeper.feature.home.HomeUIState
import com.memory.keeper.feature.home.HomeViewModel
import com.memory.keeper.feature.login.SignUpBottomButton
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.feature.prompt.PromptUIState
import com.memory.keeper.feature.util.MySpeechRecognizer
import com.memory.keeper.feature.util.PermissionDialog
import com.memory.keeper.feature.util.TTSManager
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.delay


@Composable
fun ChatScreen(
){
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            isPatient = true,
            isHome = true,
            userName = "정진이",
            isExpanded = false,
            patients = null,
            onClick = { index ->
                //viewModel.setSelectedUserId(index)
            },
            onDismiss = {
                //isExpanded = false
            }
        )
        ChatScreenContent(
            onClickBtn = {
                composeNavigator.navigate(Screen.AIChatScreen)
            }
        )
    }
}

@Composable
private fun ChatScreenContent(
    onClickBtn: () -> Unit,
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
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
        modifier = Modifier.fillMaxSize().padding(Dimens.gapLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp).background(
                    shape = CircleShape,
                    color = MemoryTheme.colors.primary
                ).clickable(
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
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "대화 시작하기",
                style = MemoryTheme.typography.headlineLarge,
            )
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