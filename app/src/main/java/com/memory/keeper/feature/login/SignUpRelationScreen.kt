package com.memory.keeper.feature.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.core.Dimens
import com.memory.keeper.navigation.Screen
import com.memory.keeper.navigation.currentComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun SignUpRelationScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    name: String,
    userName: String,
    userId: Long,
    title: String = "회원가입",
    fromMy: Boolean = false
){
    var enabled by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val composeNavigator = currentComposeNavigator
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val type = listOf("SON","DAUGHTER","RELATIVE","FRIEND","THERAPIST","OTHER")
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxPhoneWidth).windowInsetsPadding(
            WindowInsets.systemBars).background(MemoryTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignUpTopBar(title)
        if(uiState == SignUpUIState.Loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    color = MemoryTheme.colors.primary
                )
            }
        }else{
            SignUpRelationContent(
                modifier = Modifier.weight(1f),
                name = name,
                userName = userName,
                selectedIndex = selectedIndex,
                onClick = {
                    selectedIndex = it
                    enabled = it != -1
                }
            )
            SignUpBottomButton(
                enabled = enabled,
                onClick = {
                    viewModel.requestRelationship(userId, type[selectedIndex])
//                    composeNavigator.navigate(
//                        Screen.SignUpFinish("박유진")
//                    )
                },
                title = "다음"
            )
        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpUIEvent.NavigateToNext -> {
                    if(!fromMy){
                        composeNavigator.navigate(
                            Screen.SignUpFinish(name)
                        )
                    }else{
                        Toast.makeText(context, "요청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                is SignUpUIEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun SignUpRelationContent(
    modifier: Modifier,
    name: String,
    userName: String,
    selectedIndex: Int,
    onClick: (Int) -> Unit
){
    val relationship = listOf("아들", "딸", "친척", "친구", "치료사", "기타")
    Column(
        modifier = modifier.widthIn(Dimens.maxPhoneWidth).padding(
            horizontal = Dimens.gapLarge),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${name}님과 ${userName}님의\n관계를 선택해주세요",
            style = MemoryTheme.typography.headlineLarge,
            color = MemoryTheme.colors.textPrimary
        )
        Column(
            modifier = modifier.widthIn(Dimens.maxPhoneWidth),
            verticalArrangement = Arrangement.Center
        ) {
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
                            onClick(i)
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
                            onClick(i)
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
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PHONE)
@Composable
fun SignUpRelationScreenPreview(){
    MemoryTheme {
        SignUpRelationScreen(
            name = "박유진",
            userName = "박성근",
            userId = 1L
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.TABLET)
@Composable
fun SignUpRelationScreenTabletPreview(){
    MemoryTheme {
        SignUpRelationScreen(
            name = "박유진",
            userName = "박성근",
            userId = 1L
        )
    }
}