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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.ui.theme.MemoryTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomeScreen(
    //homeViewModel: HomeViewModel = hiltViewModel()
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            selectedIndex = selectedIndex,
            onClick = { index ->
                selectedIndex = index
            },
        )
        when(selectedIndex){
            0 -> HomeContent()
            1 -> HomeUserContent()
        }
    }
}

@Composable
fun HomeContent(){
    var showDetail by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(-1) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isTablet = with(adaptiveInfo) {
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    }
    if(!isTablet){
        LazyColumn(
            modifier = Modifier.padding(Dimens.gapLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapHuge)
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
                    DayDetailScreen()
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
                        DayDetailScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeUserContent(){
    Column(
        modifier = Modifier.padding(Dimens.gapLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapHuge)
    ) {
        AIChatScreen()
    }
}

@Composable
fun SimpleCalendar(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
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
                                            onClick(day)
                                        },
                                    contentAlignment = Alignment.Center
                                ){

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
                }
            )
        }
    }
}

@Composable
fun DatePickerDialog(
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