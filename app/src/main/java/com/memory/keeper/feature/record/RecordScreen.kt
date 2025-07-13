package com.memory.keeper.feature.record

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import coil.compose.AsyncImage
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.home.SimpleCalendar
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.launch

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel(),
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val role by viewModel.role.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isPatient = role == "PATIENT",
            userName = userName,
            selectedIndex = selectedIndex,
            onClick = { index ->
                selectedIndex = index
            },
        )
        RecordContent()
    }
}


@Composable
private fun RecordContent(){
    var showDetail by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(-1) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isTablet = with(adaptiveInfo) {
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }
    if(!isTablet){
        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(
                modifier = Modifier.padding(horizontal = Dimens.gapMedium, vertical = Dimens.gapLarge),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
            ) {
                item {
                    SimpleCalendar(
                        onClick = {
                            showDetail = true
                            selectedDay = it
                        }
                    )
                }
                if(showDetail && selectedDay != -1){
                    item {
                        RecordDetailScreen()
                    }
                }
            }
            if(!isAtTop){
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Dimens.gapLarge),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    containerColor = Color.Unspecified,
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.go_up),
                        contentDescription = "go up",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }else{
        Row(
            modifier = Modifier.padding(Dimens.gapLarge),
        ) {
            SimpleCalendar(
                modifier = Modifier.weight(1f),
                onClick = {
                    showDetail = true
                    selectedDay = it
                }
            )
            if(showDetail && selectedDay != -1){
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        RecordDetailScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordDetailScreen(
    chats: List<String> = emptyList(),
    images: List<String> = emptyList(),
    video: String? = null,
){
    val pagerState = rememberPagerState(pageCount = {
        images.size
    })
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.gapLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "대화 기록",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textPrimary
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                chats.forEach { chat ->
                    Text(
                        text = chat,
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textPrimary,
                        softWrap = true
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ) {
            Text(
                text = "이미지",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textPrimary
            )
            if (images.isNotEmpty()) {
                HorizontalPager(state = pagerState){ page ->
                    val image = images[page]
                    AsyncImage(
                        model = image,
                        contentDescription = "image",
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                            .clip(shape = RoundedCornerShape(Dimens.cornerRadius))
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(images.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MemoryTheme.colors.textPrimary
                                    else MemoryTheme.colors.divider
                                )
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth().height(200.dp)
                        .border(border = BorderStroke(
                            width = 1.dp,
                            color = MemoryTheme.colors.divider
                        ), shape = RoundedCornerShape(Dimens.cornerRadius)),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "이미지가 없습니다.",
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textSecondary
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
        ){
            Text(
                text = "영상",
                style = MemoryTheme.typography.header,
                color = MemoryTheme.colors.textPrimary
            )
            video?.let {
                ExoVideoPlayer(it.toUri())
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth().height(200.dp)
                        .border(border = BorderStroke(
                            width = 1.dp,
                            color = MemoryTheme.colors.divider
                        ), shape = RoundedCornerShape(Dimens.cornerRadius)),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "영상이 없습니다.",
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExoVideoPlayer(videoUri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

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
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
}