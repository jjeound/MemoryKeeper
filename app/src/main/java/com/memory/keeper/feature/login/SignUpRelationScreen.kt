package com.memory.keeper.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpRelationScreen(
    name: String,
    userName: String
){
    val enabled = remember { mutableStateOf(false) }
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar()
        SignUpRelationContent(
            modifier = Modifier.weight(1f),
            enabled = enabled,
            name = name,
            userName = userName
        )
        SignUpBottomButton(
            enabled = enabled,
            onClick = {
                composeNavigator.navigate(
                    Screen.Home
                )
            },
            title = "다음"
        )
    }
}

@Composable
fun SignUpRelationContent(
    modifier: Modifier,
    enabled : MutableState<Boolean>,
    name: String,
    userName: String
){
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val relationship = listOf("아들", "딸", "친척", "친구", "요양보호사", "기타")
    if(selectedIndex != -1) {
        enabled.value = true
    }
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${name}님과 ${userName}님의\n관계를 선택해주세요",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapHuge)
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
                        selectedIndex = i
                        enabled.value = true
                    }
                ) {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = relationship[i],
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
                        selectedIndex = i
                        enabled.value = true
                    }
                ) {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = relationship[i],
                            style = MemoryTheme.typography.option,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpRelationScreenPreview(){
    MemoryTheme {
        SignUpRelationScreen(
            name = "박유진",
            userName = "박성근"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
@Composable
fun SignUpRelationScreenTabletPreview(){
    MemoryTheme {
        SignUpRelationScreen(
            name = "박유진",
            userName = "박성근"
        )
    }
}