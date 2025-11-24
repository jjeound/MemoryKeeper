package com.memory.keeper.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memory.keeper.core.Dimens
import com.memory.keeper.feature.login.SignUpTopBar
import com.memory.keeper.ui.theme.MemoryTheme
import kotlinx.coroutines.delay

@Composable
fun VideoScreen(
) {
    Column(
        modifier = Modifier
            .fillMaxSize().background(
                color = MemoryTheme.colors.surface
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SignUpTopBar("")
        VideoScreenContent()
    }
}

@Composable
fun VideoScreenContent(){
    var shouldShowVideo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(3000)
        shouldShowVideo = true
    }
    Column(
        modifier = Modifier.padding(Dimens.gapLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapHuge)
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 20.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            if(!shouldShowVideo){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "영상을 생성하고 있어요",
                    style = MemoryTheme.typography.headlineLarge,
                    color = MemoryTheme.colors.textPrimary
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ){
                if(!shouldShowVideo){
                    CircularProgressIndicator(
                        color = MemoryTheme.colors.primary
                    )
                }else {
                    ExoVideoPlayer()
                }
            }
        }
    }
}