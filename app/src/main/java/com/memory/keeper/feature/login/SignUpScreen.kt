package com.memory.keeper.feature.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
){
    val context = LocalContext.current
    val composeNavigator = currentComposeNavigator
    val name by viewModel.name.collectAsStateWithLifecycle()
    val hasSignedUp by viewModel.hasSignedUp.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp).windowInsetsPadding(WindowInsets.systemBars),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "소중한 기억들을 회상하며\n" +
                    "치매를 극복할 수 있게 도와드립니다",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary,
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapLarge)
        )
        AsyncImage(
            model = R.drawable.login_logo,
            contentDescription = "login logo",
        )
        Spacer(
            modifier = Modifier.height(Dimens.gapLarge)
        )
        Image(
            modifier = Modifier.width(350.dp).height(90.dp).clickable(
                onClick = {
                    kakaoLogin(context){ code ->
                        viewModel.login(code)
                    }
                    //viewModel.testLogin("1")
//                    composeNavigator.navigate(Screen.Home){
//                        popUpTo(0) { inclusive = true } // 모든 백스택 제거
//                        launchSingleTop = true
//                    }
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
            painter = painterResource(id = R.drawable.kakao_login_large_wide),
            contentDescription = "kakao_login"
        )
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when(event){
                is SignUpUIEvent.NavigateToNext -> {
                    name?.let {
                        if(hasSignedUp){
                            composeNavigator.navigate(Screen.Home){
                                popUpTo(0) { inclusive = true } // 모든 백스택 제거
                                launchSingleTop = true
                            }
                        }else{
                            composeNavigator.navigate(Screen.SelectMode(it))
                        }
                        composeNavigator.navigate(Screen.SelectMode(it))
                    }
                }
                is SignUpUIEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
            
        }
    }
}

private fun kakaoLogin(context: Context, onResult: (String) -> Unit){
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.d("Login", error.message.toString())
        } else if (token != null) {
            onResult(token.accessToken)
            Log.d("Login", "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }
// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.d("Login", "카카오톡으로 로그인 실패")
                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }
                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                onResult(token.accessToken)
                Log.d("Login", "카카오톡으로 로그인 성공 ${token.accessToken}")
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
@Composable
fun SignUpScreenPreview() {
    MemoryTheme {
        SignUpScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpScreenPhonePreview() {
    MemoryTheme {
        SignUpScreen()
    }
}