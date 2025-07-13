package com.memory.keeper.feature.my

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.memory.keeper.R
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel(),
){
    val composeNavigator = currentComposeNavigator
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        MyTopBar()
        MyContent(
            userName = userName,
            logout = viewModel::logout,
        )
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect {
            when (it) {
                is MyUIEvent.NavigateToLogin -> {
                    composeNavigator.navigate(
                        Screen.SignUp
                    )
                }
                is MyUIEvent.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun MyContent(
    userName: String?,
    logout: () -> Unit,
) {
    val composeNavigator = currentComposeNavigator
    Column(
        modifier = Modifier.widthIn(Dimens.maxPhoneWidth).padding(Dimens.gapLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.gapMedium),
    ){
        MyAccountContainer(userName)
        MyOptionBox(
            title = "로그아웃",
            onClick = logout,
        )
        MyOptionBox(
            title = "관계 설정하기",
            onClick = {
                composeNavigator.navigate(
                    Screen.SearchUser(
                        name = userName ?: "USER",
                    )
                )
            },
        )
        MyOptionBox(
            title = "개인정보처리방침",
            onClick = {
            },
        )
        MyOptionBox(
            title = "회원탈퇴",
            onClick = {
            },
        )
    }
}

@Composable
fun MyAccountContainer(
    name: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Dimens.gapMedium),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier.size(48.dp),
                model = R.drawable.logo,
                contentDescription = "profile",
            )
            Text(
                text = "${name ?: "USER"}님",
                style = MemoryTheme.typography.body,
                color = MemoryTheme.colors.textPrimary,
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MemoryTheme.colors.divider,
        thickness = 1.dp
    )
}

@Composable
fun MyOptionBox(
    title: String,
    onClick: () -> Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            onClick()
        }.padding(Dimens.gapLarge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MemoryTheme.typography.body,
            color = MemoryTheme.colors.textPrimary,
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
            contentDescription = "navigate",
            tint = MemoryTheme.colors.iconDefault
        )
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MemoryTheme.colors.divider,
        thickness = 1.dp
    )
}