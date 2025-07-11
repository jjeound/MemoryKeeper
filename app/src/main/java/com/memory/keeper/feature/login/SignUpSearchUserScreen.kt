package com.memory.keeper.feature.login

import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpSearchUserScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    name: String,
){
    var enabled by remember { mutableStateOf(false) }
    val composeNavigator = currentComposeNavigator
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val userSearched by viewModel.userSearched.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar()
        if(uiState == SignUpUiState.Loading){
            CircularProgressIndicator(
                modifier = Modifier.weight(1f),
                color = MemoryTheme.colors.primary,
            )
        }else{
            SearchUserContent(
                modifier = Modifier.weight(1f),
                email = email,
                onChange = {
                    email = it
                    enabled = it.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                isError = isError
            )
            SignUpBottomButton(
                enabled = enabled,
                onClick = {
                    viewModel.searchUserByEmail(email)
                },
                title = "다음"
            )
        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpUIEvent.NavigateToNext -> {
                    userSearched?.let {
                        composeNavigator.navigate(
                            Screen.SetRelation(name, it.name, it.id)
                        )
                    }
                }
                is SignUpUIEvent.ShowToast -> {
                    // Handle error
                    isError = true
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun SearchUserContent(
    modifier: Modifier,
    email: String,
    onChange: (String) -> Unit,
    isError: Boolean
){
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "관계 설정하기",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapHuge)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().semantics{
                contentType = ContentType.EmailAddress
            },
            value = email,
            onValueChange = { text ->
                onChange(text)
            },
            placeholder = {
                Text(
                    text = "환자의 가입 이메일을 입력해주세요.",
                    style = MemoryTheme.typography.body,
                    color = MemoryTheme.colors.textSecondary
                )
            },
            trailingIcon = {
                if(email.isNotEmpty()){
                    IconButton (
                        onClick = {
                            onChange("")
                        }
                    ){
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.setting),
                            contentDescription = "delete"
                        )
                    }
                }
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
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            singleLine = true,
            maxLines = 1,
            isError = isError
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpSearchScreenPreview() {
    MemoryTheme {
        SignUpSearchUserScreen(
            name = "홍길동"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
@Composable
fun SignUpSearchScreenTabletPreview() {
    MemoryTheme {
        SignUpSearchUserScreen(
            name = "홍길동"
        )
    }
}