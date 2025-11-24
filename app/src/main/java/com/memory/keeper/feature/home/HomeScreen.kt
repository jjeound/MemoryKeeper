package com.memory.keeper.feature.home

import android.widget.NumberPicker
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import coil.compose.AsyncImage
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.DailyResponse
import com.memory.keeper.data.dto.response.MonthlyResponse
import com.memory.keeper.feature.chat.ExoVideoPlayer
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.feature.prompt.ContentBox
import com.memory.keeper.feature.prompt.MemoryTextField
import com.memory.keeper.feature.util.PreviewTheme
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    var isExpanded by remember { mutableStateOf(false) }
    val role by viewModel.role.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val monthResponse by viewModel.monthlyResponse.collectAsStateWithLifecycle()
    val dailyResponse by viewModel.dailyResponse.collectAsStateWithLifecycle()
    val patientNames by viewModel.patientNames.collectAsStateWithLifecycle()
    val selectedUserId by viewModel.selectedUserId.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

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
            isExpanded = isExpanded,
            patients = patientNames,
            onClick = { index ->
                viewModel.setSelectedUserId(index)
            },
            onDismiss = {
                isExpanded = false
            }
        )
        HomeScreenContent(
            isHealer = role == "HEALER",
            monthResponse = monthResponse,
            dailyResponse = dailyResponse,
            getMonthlyRecord = viewModel::getMonthlyRecord,
            getDailyRecord = { date ->
                selectedUserId?.let { id ->
                    viewModel.getDailyRecord(date, id)
                } ?: run {
                    viewModel.getDailyRecord(date, userId!!)
                }
            },
            onSaveFeedback = viewModel::saveFeedback,
        )
    }
}

@Composable
private fun HomeScreenContent(
    isHealer: Boolean = false,
    monthResponse : List<MonthlyResponse> = emptyList(),
    dailyResponse: DailyResponse? = null,
    getMonthlyRecord: (String) -> Unit,
    getDailyRecord: (String) -> Unit,
    onSaveFeedback: (String, Long, String) -> Unit,
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
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 40.dp)
                    .imePadding(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(20.dp),
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
                            dailyResponse = dailyResponse,
                            onSaveFeedback = onSaveFeedback
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
                            onSaveFeedback = onSaveFeedback
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
    onSaveFeedback: (String, Long, String) -> Unit,
    //통계 추가,
){
    val option = listOf("전체", "대화", "통계")
    var selectedOption by remember { mutableStateOf(option[0]) }
    var feedback by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp),
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MemoryTheme.colors.surface,
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    option.forEach {
                        Text(
                            text = it,
                            style = MemoryTheme.typography.headlineSmall,
                            color = if(selectedOption == it) MemoryTheme.colors.primary else MemoryTheme.colors.textSecondary,
                            modifier = Modifier.clickable{
                                selectedOption = it
                            }
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MemoryTheme.colors.surface,
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    dailyResponse.conversations.forEach { conversation ->
                        if(conversation.contains("assistant")){
                            Text(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                                text = conversation.replace("assistant:", ""),
                                style = MemoryTheme.typography.body,
                                color = MemoryTheme.colors.textPrimary,
                            )
                        } else {
                            Column(
                                modifier = Modifier.wrapContentSize().background(
                                    color = Color(0xFFEFEFEF),
                                    shape = RoundedCornerShape(6.dp)
                                ).align(Alignment.End),
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 14.dp
                                    ),
                                    text = conversation.replace("user:", ""),
                                    style = MemoryTheme.typography.body,
                                    color = MemoryTheme.colors.textPrimary,
                                )
                            }
                        }
                    }
                    if (dailyResponse.imageUrls != null && dailyResponse.imageUrls.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = R.drawable.ai_image,
                                contentDescription = "image",
                                alignment = Alignment.Center,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(Dimens.cornerRadius))
                            )
                            Text(
                                text = "가족사진 - 1970년대 추억",
                                style = MemoryTheme.typography.description,
                                color = MemoryTheme.colors.textPrimary
                            )
                            Text(
                                text = "AI가 생성한 이미지",
                                style = MemoryTheme.typography.badge,
                                color = MemoryTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MemoryTheme.colors.surface,
                ),
            ){
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "전체",
                        style = MemoryTheme.typography.headlineSmall,
                        color = MemoryTheme.colors.primary
                    )
                    Text(
                        text = "이미지",
                        style = MemoryTheme.typography.headlineSmall,
                        color = MemoryTheme.colors.textSecondary
                    )
                    Text(
                        text = "영상",
                        style = MemoryTheme.typography.headlineSmall,
                        color = MemoryTheme.colors.textSecondary
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = R.drawable.ai_image,
                    contentDescription = "image",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                        .clip(shape = RoundedCornerShape(Dimens.cornerRadius))
                )
                ExoVideoPlayer()
            }
        }
        if(isHealer){
            ContentBox(
                header = "피드백",
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MemoryTextField(
                            text = feedback,
                            placeHolder = Pair("", "피드백을 입력해주세요."),
                            onChange = {
                                feedback = it
                            },
                            focusManager = LocalFocusManager.current
                        )
                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = {
                                onSaveFeedback(feedback, dailyResponse.userId, dailyResponse.dailyDayRecording)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MemoryTheme.colors.primary,
                                contentColor = MemoryTheme.colors.textOnPrimary,
                            ),
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "저장",
                                style = MemoryTheme.typography.button,
                            )
                        }
                    }
                }
            )
        }
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
                            val formattedDay = String.format("%02d", day)
                            val dayRecord = monthResponse.firstOrNull { record ->
                                record.monthlyDayRecording == "${currentMonth}-${formattedDay}"
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
                                            if(dayRecord != null){
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
                .background(color = MemoryTheme.colors.onPrimary, shape = RoundedCornerShape(Dimens.cornerRadius))
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
    PreviewTheme {
        HomeScreenContent(
            isHealer = true,
            monthResponse = emptyList(),
            dailyResponse = null,
            getMonthlyRecord = {},
            getDailyRecord = {},
            onSaveFeedback = {_,_,_ ->}
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
fun RecordDetailScreenPreview() {
    PreviewTheme {
        RecordDetailScreen(
            isHealer = true,
            dailyResponse = DailyResponse(
                conversations = listOf(
                    "assistant: 안녕하세요. 오늘 기분이 어떠신가요?",
                    "user: 오늘은 정말 좋은 하루였어요!",
                    "assistant: 그렇군요. 어떤 일이 있었나요?",
                    "user: 친구들과 함께 산책을 했어요."
                ),
                createdAt = "",
                dailyDayRecording ="",
                feedback = null,
                id = 0L,
                userId = 0L,
                imageUrls = listOf(),
                updatedAt = "",
                videoUrl = null
            ),
            onSaveFeedback = {_,_,_ ->}
        )
    }
}

@Preview
@Composable
fun DatePickerDialogPreview(){
    PreviewTheme {
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