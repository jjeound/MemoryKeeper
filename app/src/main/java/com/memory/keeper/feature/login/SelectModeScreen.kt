package com.memory.keeper.feature.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SelectModeScreen(){
    val enabled = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar()
        SelectModeContent(
            modifier = Modifier.weight(1f),
            enabled
        )
        SignUpBottomButton(
            enabled = enabled,
            onClick = {},
            title = "다음"
        )
    }
}

@Composable
fun SelectModeContent(
    modifier: Modifier,
    enabled : MutableState<Boolean>
){
    var selectedIndex by remember { mutableIntStateOf(0) }
    if(selectedIndex != 0) {
        enabled.value = true
    }
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "보호자 / 사용자 모드 중 하나를 선택해주세요",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapHuge)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if(selectedIndex == 1) MemoryTheme.colors.optionFocused else MemoryTheme.colors.optionUnfocused,
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                border = BorderStroke(width = 1.dp, color = if(selectedIndex == 1) MemoryTheme.colors.optionBorderFocused else MemoryTheme.colors.optionBorderUnfocused),
                onClick = {
                    selectedIndex = 1
                }
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.gapMedium),
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.gapLarge).weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
                    ) {
                        Text(
                            text = "보호자",
                            style = MemoryTheme.typography.headlineLarge,
                            color = if(selectedIndex == 1) MemoryTheme.colors.primary else MemoryTheme.colors.textPrimary
                        )
                        Text(
                            text = "기억지기 사용을 위해서는 보호자의 사진 업로드 및 설정을 필요로 합니다",
                            style = MemoryTheme.typography.body,
                            color = if(selectedIndex == 1) MemoryTheme.colors.primary else MemoryTheme.colors.textPrimary,
                            softWrap = true
                        )
                    }
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.parent),
                        contentDescription = "parent icon",
                        colorFilter = ColorFilter.tint(
                            if(selectedIndex == 1) MemoryTheme.colors.primary else MemoryTheme.colors.optionBorderUnfocused
                        )
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if(selectedIndex == 2) MemoryTheme.colors.optionFocused else MemoryTheme.colors.optionUnfocused,
                ),
                shape = RoundedCornerShape(Dimens.cornerRadius),
                border = BorderStroke(width = 1.dp, color = if(selectedIndex == 2) MemoryTheme.colors.optionBorderFocused else MemoryTheme.colors.optionBorderUnfocused),
                onClick = {
                    selectedIndex = 2
                }
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.gapMedium),
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.gapLarge).weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
                    ) {
                        Text(
                            text = "기억지기",
                            style = MemoryTheme.typography.headlineLarge,
                            color = if(selectedIndex == 2) MemoryTheme.colors.primary else MemoryTheme.colors.textPrimary
                        )
                        Text(
                            text = "어플을 통해 치매예방을 필요로 하는 분을 위한 어플사용 모드 입니다",
                            style = MemoryTheme.typography.body,
                            color = if(selectedIndex == 2) MemoryTheme.colors.primary else MemoryTheme.colors.textPrimary,
                            softWrap = true
                        )
                    }
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.keeper),
                        contentDescription = "parent icon",
                        colorFilter = ColorFilter.tint(
                            if(selectedIndex == 2) MemoryTheme.colors.primary else MemoryTheme.colors.optionBorderUnfocused
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SelectModeScreenPreview(){
    MemoryTheme {
        SelectModeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
@Composable
fun SelectModeTabletScreenPreview(){
    MemoryTheme {
        SelectModeScreen()
    }
}