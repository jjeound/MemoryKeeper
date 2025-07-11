package com.memory.keeper.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpFinishScreen(
    name: String,
) {
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar()
        SignUpContent(
            modifier = Modifier.weight(1f),
            name = name
        )
        SignUpBottomButton(
            enabled = true,
            onClick = {
                composeNavigator.navigate(
                    Screen.Home
                ){
                    popUpTo(0) { inclusive = true } // 모든 백스택 제거
                    launchSingleTop = true
                }
            },
            title = "시작하기"
        )
    }
}

@Composable
fun SignUpContent(
    modifier: Modifier,
    name: String
) {
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium)
        ) {
            Text(
                text = "${name}님\n" +
                        "회원가입을 축하드립니다!",
                style = MemoryTheme.typography.headlineLarge,
                color = MemoryTheme.colors.textPrimary
            )
            Text(
                text = "기억지기 어플을 통해 치매를 예방하세요",
                style = MemoryTheme.typography.headlineSmall,
                color = MemoryTheme.colors.textThird
            )
        }
        AsyncImage(
            model = R.drawable.logo,
            contentDescription = "logo",
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpFinishScreenPreview() {
    MemoryTheme {
        SignUpFinishScreen(
            name = "홍길동"
        )
    }
}