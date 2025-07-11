package com.memory.keeper.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpSetNameScreen(){
    val enabled = remember { mutableStateOf(false) }
    val composeNavigator = currentComposeNavigator
    val name = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar()
        SetNameContent(
            modifier = Modifier.weight(1f),
            enabled,
            name,
            userName
        )
        SignUpBottomButton(
            enabled = enabled,
            onClick = {
                composeNavigator.navigate(
                    Screen.SetRelation(name.value, userName.value)
                )
            },
            title = "다음"
        )
    }
}

@Composable
fun SetNameContent(
    modifier: Modifier,
    enabled: MutableState<Boolean>,
    name: MutableState<String>,
    userName: MutableState<String>
){
    val focusManager = LocalFocusManager.current
    enabled.value = name.value.length in 1..10 && userName.value.length in 1..10
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "만나서 반가워요! \n어떻게 불러드리면 될까요?",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapHuge)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).semantics{
                contentType = ContentType.Username
            },
            value = name.value,
            onValueChange = { text ->
                name.value = text
            },
            placeholder = {
                Text(
                    text = "본인의 이름을 입력해주세요",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textSecondary
                )
            },
            suffix = {
                Text(
                    text = "${name.value.length}/10",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MemoryTheme.colors.textSecondary,
                unfocusedBorderColor = MemoryTheme.colors.textSecondary,
                focusedTextColor = MemoryTheme.colors.textPrimary,
                unfocusedTextColor = MemoryTheme.colors.textPrimary,
                focusedContainerColor = MemoryTheme.colors.surface,
                unfocusedContainerColor = MemoryTheme.colors.surface,
                cursorColor = MemoryTheme.colors.textPrimary,
                errorCursorColor = MemoryTheme.colors.red,
                errorBorderColor = MemoryTheme.colors.red,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            maxLines = 1,
            isError = name.value.length > 10
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapHuge)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().widthIn(Dimens.maxPhoneWidth).semantics{
                contentType = ContentType.Username
            },
            value = userName.value,
            onValueChange = { text ->
                userName.value = text
            },
            placeholder = {
                Text(
                    text = "사용자 이름을 입력해주세요",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textSecondary
                )
            },
            suffix = {
                Text(
                    text = "${userName.value.length}/10",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MemoryTheme.colors.textSecondary,
                unfocusedBorderColor = MemoryTheme.colors.textSecondary,
                focusedTextColor = MemoryTheme.colors.textPrimary,
                unfocusedTextColor = MemoryTheme.colors.textPrimary,
                focusedContainerColor = MemoryTheme.colors.surface,
                unfocusedContainerColor = MemoryTheme.colors.surface,
                cursorColor = MemoryTheme.colors.textPrimary,
                errorCursorColor = MemoryTheme.colors.red,
                errorBorderColor = MemoryTheme.colors.red,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            maxLines = 1,
            isError = userName.value.length > 10
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpSetNameScreenPreview() {
    MemoryTheme {
        SignUpSetNameScreen()
    }
}