package com.memory.keeper.feature.home

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
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
import com.memory.keeper.data.dto.response.DailyResponse
import com.memory.keeper.data.dto.response.MonthlyResponse
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val role by viewModel.role.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val monthResponse by viewModel.monthlyResponse.collectAsStateWithLifecycle()
    val dailyResponse by viewModel.dailyResponse.collectAsStateWithLifecycle()
    val patientNames by viewModel.patientNames.collectAsStateWithLifecycle()
    val selectedUserId by viewModel.selectedUserId.collectAsStateWithLifecycle()

    LaunchedEffect(role) {
        if(role != null && role != "PATIENT"){
            viewModel.getMyPatients()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isPatient = role == "PATIENT",
            isHome = true,
            userName = userName,
            patientName = if(patientNames.isNotEmpty()) patientNames[selectedIndex] else null,
            selectedIndex = selectedIndex,
            onClick = { index ->
                selectedIndex = index
                viewModel.setSelectedUserId(index)
            },
        )
        when(selectedIndex){
            0 -> RecordContent(
                isHealer = role == "HEALER",
                monthResponse = monthResponse,
                dailyResponse = dailyResponse,
                getMonthlyRecord = viewModel::getMonthlyRecord,
                getDailyRecord = { date ->
                    selectedUserId?.let { id ->
                        viewModel.getDailyRecord(date, id)
                    }
                }
            )
            1 -> HomeUserContent(
                viewModel = viewModel
            )
        }
    }
}



@Composable
private fun HomeUserContent(viewModel: HomeViewModel){
    Column(
        modifier = Modifier.padding(Dimens.gapLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapHuge)
    ) {
        AIChatScreen(
            viewModel = viewModel
        )
    }
}

@Composable
private fun RecordContent(
    isHealer: Boolean = false,
    monthResponse : List<MonthlyResponse> = emptyList(),
    dailyResponse: DailyResponse? = null,
    getMonthlyRecord: (String) -> Unit,
    getDailyRecord: (String) -> Unit
){
    var showDetail by remember { mutableStateOf(false) }
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
                modifier = Modifier.padding(horizontal = Dimens.gapMedium, vertical = Dimens.gapLarge)
                    .imePadding(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
            ) {
                item {
                    SimpleCalendar(
                        monthResponse = monthResponse,
                        onClick = {
                            showDetail = true
                            getDailyRecord(it)
                        },
                        onMonthChange = getMonthlyRecord
                    )
                }
                if(showDetail && dailyResponse != null){
                    item {
                        RecordDetailScreen(
                            isHealer = isHealer,
                            dailyResponse = dailyResponse
                        )
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
                monthResponse = monthResponse,
                onMonthChange = getMonthlyRecord,
                onClick = {
                    showDetail = true
                    getDailyRecord(it)
                }
            )
            if(showDetail && dailyResponse != null){
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        RecordDetailScreen(
                            isHealer = isHealer,
                            dailyResponse = dailyResponse,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordDetailScreen(
    isHealer: Boolean,
    dailyResponse: DailyResponse,
){
    val imagePagerState = rememberPagerState(pageCount = {
        dailyResponse.imageUrls.size
    })
    val videoPagerState = rememberPagerState(pageCount = {
        dailyResponse.videoUrls.size
    })
    var feedback by remember { mutableStateOf("") }
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
                verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
            ) {
                Text(
                    text = dailyResponse.conversation,
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary,
                    softWrap = true
                )
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
            if (dailyResponse.imageUrls.isNotEmpty()) {
                HorizontalPager(state = imagePagerState){ page ->
                    val image = dailyResponse.imageUrls[page]
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
                    repeat(dailyResponse.imageUrls.size) { index ->
                        val isSelected = imagePagerState.currentPage == index
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
            if(dailyResponse.videoUrls.isNotEmpty()) {
                HorizontalPager(state = videoPagerState){ page ->
                    val video = dailyResponse.videoUrls[page]
                    ExoVideoPlayer(video.toUri())
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(dailyResponse.videoUrls.size) { index ->
                        val isSelected = videoPagerState.currentPage == index
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
                        text = "영상이 없습니다.",
                        style = MemoryTheme.typography.body,
                        color = MemoryTheme.colors.textSecondary
                    )
                }
            }
        }
        if(isHealer){
            ContentBox(
                title = "피드백",
                text = feedback,
                placeHolder = "피드백을 입력해 주세요",
                onChange = {
                    feedback = it
                },
                focusManager = LocalFocusManager.current
            )
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

@Composable
private fun SimpleCalendar(
    modifier: Modifier = Modifier,
    monthResponse : List<MonthlyResponse> = emptyList(),
    onMonthChange: (String) -> Unit,
    onClick: (String) -> Unit
) {
    val currentDate = LocalDate.now()
    var currentMonthValue by remember { mutableIntStateOf(currentDate.monthValue) } // 1 ~ 12
    var currentYear by remember { mutableIntStateOf(currentDate.year) }

    val currentMonth = YearMonth.of(currentYear, currentMonthValue)
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    var showDatePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .clickable{
                    showDatePickerDialog = true
                },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${currentYear}년 ${currentMonthValue}월",
                style = MemoryTheme.typography.keywordLarge,
                color = MemoryTheme.colors.textPrimary,
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.bottom_arrow),
                contentDescription = "bottom arrow",
                tint = Color.Unspecified,
            )
        }
        Row {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        val totalCells = firstDayOfMonth + daysInMonth
        val weeks = (totalCells + 6) / 7

        var day = 1
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ) {
            repeat(weeks) {
                Row {
                    repeat(7) { dayOfWeek ->
                        if (it == 0 && dayOfWeek < firstDayOfMonth || day > daysInMonth) {
                            Box(modifier = Modifier.weight(1f)) { }
                        } else {
                            val dayRecord = monthResponse.firstOrNull { record ->
                                record.monthlyDayRecording == "${currentMonth}-${day}"
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(Dimens.circle)
                                        .padding(Dimens.gapSmall)
                                        .background(
                                            color = MemoryTheme.colors.calendarBg,
                                            shape = CircleShape
                                        ).clickable{
                                            if(dayRecord != null ){
                                                onClick(dayRecord.monthlyDayRecording)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ){
                                    if(dayRecord != null && dayRecord.imageUrl != null){
                                        AsyncImage(
                                            model = dayRecord.imageUrl,
                                            contentDescription = "image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(Dimens.circle)
                                                .clip(CircleShape)
                                        )
                                    }
                                    if(day == 15){
                                        AsyncImage(
                                            model = R.drawable.ai_image,
                                            contentDescription = "image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(Dimens.circle)
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                                Text(
                                    text = "${day++}",
                                    style = MemoryTheme.typography.body,
                                    color = MemoryTheme.colors.textPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
        if(showDatePickerDialog){
            DatePickerDialog(
                year = currentYear,
                month = currentMonthValue,
                onCancel = {
                    showDatePickerDialog = false
                },
                onConfirm = { newYear, newMonth ->
                    showDatePickerDialog = false
                    currentYear = newYear
                    currentMonthValue = newMonth
                    onMonthChange("$newYear-$newMonth")
                }
            )
        }
    }
}

@Composable
private fun DatePickerDialog(
    year: Int,
    month: Int,
    onCancel: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var currentYear by remember { mutableIntStateOf(year) }
    var currentMonthValue by remember { mutableIntStateOf(month) }
    Dialog(
        onDismissRequest = onCancel,
    ) {
        Column(
            modifier = Modifier
                .background(color = MemoryTheme.colors.box, shape = RoundedCornerShape(Dimens.cornerRadius))
                .padding(Dimens.gapHuge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "언제로 이동할까요?",
                style = MemoryTheme.typography.headlineSmall,
                color = MemoryTheme.colors.textOnPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(
                    modifier = Modifier.weight(1f),
                    factory = {context ->
                        NumberPicker(context).apply {
                            minValue = 2000
                            maxValue = 2025
                            value = year
                            textColor = ContextCompat.getColor(context, R.color.white_700)
                            setOnValueChangedListener { _, _, newVal ->
                                currentYear = newVal
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(Dimens.gapLarge))

                AndroidView(
                    modifier = Modifier.weight(1f),
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 1
                            maxValue = 12
                            value = month
                            textColor = ContextCompat.getColor(context, R.color.white_700)
                            setOnValueChangedListener { _, _, newVal ->
                                currentMonthValue = newVal
                            }
                        }
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimens.gapSmall)
                        .height(48.dp),
                    shape = RoundedCornerShape(Dimens.cornerRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.buttonBorderUnfocused,
                        contentColor = MemoryTheme.colors.buttonText,
                        disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
                        disabledContentColor = MemoryTheme.colors.buttonText
                    )
                ) {
                    Text(
                        text = "취소",
                        style = MemoryTheme.typography.button,
                    )
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimens.gapSmall)
                        .height(48.dp),
                    shape = RoundedCornerShape(Dimens.cornerRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MemoryTheme.colors.primary,
                        contentColor = MemoryTheme.colors.buttonText,
                        disabledContainerColor = MemoryTheme.colors.buttonUnfocused,
                        disabledContentColor = MemoryTheme.colors.buttonText
                    )
                ) {
                    Text(
                        text = "확인",
                        style = MemoryTheme.typography.button,
                        modifier = Modifier.clickable{ onConfirm(
                            currentYear, currentMonthValue
                        ) }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun HomeScreenPreview() {
    MemoryTheme {
        HomeScreen()
    }
}

//@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
//@Composable
//fun HomeScreenTabletPreview() {
//    MemoryTheme {
//        HomeScreen()
//    }
//}

@Preview
@Composable
fun DatePickerDialogPreview(){
    MemoryTheme {
        DatePickerDialog(
            year = 2023,
            month = 10,
            onCancel = {},
            onConfirm = { newYear, newMonth ->
                // Handle the confirmed date here
            }
        )
    }
}