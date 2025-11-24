package com.memory.keeper.feature.chat

import android.view.ViewGroup
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.memory.keeper.feature.util.PreviewTheme
import com.memory.keeper.ui.theme.MemoryTheme
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.memory.keeper.R
import com.memory.keeper.feature.login.SignUpBottomButton
import kotlinx.coroutines.delay

@Composable
fun ChattingScreen(
    isListening: Boolean = false,
    onClick: () -> Unit = {},
    title: String = "",
    uiState: ChatUiState
) {
    val infiniteTransition = rememberInfiniteTransition(label = "listening")
    // 여러 레이어의 애니메이션
    val mainScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mainScale"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 2.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isListening) 0f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isListening) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // 외곽 펄스 링들
                    repeat(3) { index ->
                        val ringScale = pulseScale - (index * 0.3f)
                        val ringAlpha = (alpha - (index * 0.1f)).coerceAtLeast(0f)

                        Box(
                            modifier = Modifier
                                .size((120 + index * 40).dp)
                                .scale(ringScale)
                                .background(
                                    color = MemoryTheme.colors.primary.copy(alpha = ringAlpha),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = MemoryTheme.colors.primary.copy(alpha = ringAlpha * 0.5f),
                                    shape = CircleShape
                                )
                        )
                    }

                    // 메인 아이콘
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(mainScale)
                            .background(
                                color = MemoryTheme.colors.primary,
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                color = MemoryTheme.colors.primary.copy(alpha = 0.8f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){}
                }
            } else {
                // 대기 상태
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MemoryTheme.colors.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = MemoryTheme.colors.primary.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = R.drawable.logo,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }
            if(uiState == ChatUiState.Loading){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        DynamicPulseDot(
                            delay = index, // 0, 1, 2, 3, 4 순서
                            size = (8 + (index % 2) * 4).dp // 8, 12, 8, 12, 8 패턴
                        )
                    }
                }
            }

        }
        SignUpBottomButton(
            enabled = true,
            onClick = onClick,
            title = title
        )
    }
}

@Composable
private fun DynamicPulseDot(
    delay: Int,
    size: androidx.compose.ui.unit.Dp = 12.dp
) {
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0.2f) }
    val verticalOffset = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(delay * 150L)

        // 무한 반복 애니메이션 시작
        while (true) {
            // 스케일 애니메이션
            scale.animateTo(
                targetValue = 1.8f,
                animationSpec = tween(200, easing = LinearEasing)
            )
            scale.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(200, easing = LinearEasing)
            )

            // 알파 애니메이션
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(200, easing = LinearEasing)
            )
            alpha.animateTo(
                targetValue = 0.2f,
                animationSpec = tween(200, easing = LinearEasing)
            )

            // 수직 오프셋 애니메이션
            verticalOffset.animateTo(
                targetValue = 8f,
                animationSpec = tween(100, easing = LinearEasing)
            )
            verticalOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(100, easing = LinearEasing)
            )

            // 다음 사이클까지 대기
            delay(800L)
        }
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .offset(y = verticalOffset.value.dp)
            .scale(scale.value)
            .background(
                color = MemoryTheme.colors.primary.copy(alpha = alpha.value),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = MemoryTheme.colors.primary.copy(alpha = alpha.value * 0.5f),
                shape = CircleShape
            )
    )
}

@Composable
fun ExoVideoPlayer() {
    val context = LocalContext.current
    val uri = "android.resource://${context.packageName}/raw/video".toUri()
    val mediaItem = MediaItem.fromUri(uri)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        600
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        //Spacer(modifier = Modifier.height(16.dp))

//        Button(onClick = {
//            val intent = Intent(context, FullScreenVideoActivity::class.java).apply {
//                putExtra("VIDEO_URI", uri.toString())
//            }
//            context.startActivity(intent)
//        }) {
//            Text("Fullscreen")
//        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
private fun ChattingScreenPreview() {
    PreviewTheme {
        ChattingScreen(
            isListening = false,
            uiState = ChatUiState.Idle
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
private fun ChattingScreenListeningPreview() {
    PreviewTheme {
        ChattingScreen(
            isListening = true,
            uiState = ChatUiState.Idle
        )
    }
}