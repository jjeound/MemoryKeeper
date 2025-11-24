package com.memory.keeper.feature.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memory.keeper.feature.main.TopBar
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
){
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            isPatient = false,
            isHome = true,
            userName = "박성근",
            onClick = {}
        )
        if(uiState == RecordUiState.Loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    color = MemoryTheme.colors.primary
                )
            }
        } else {
            RecordScreenContent(
            )
        }
    }
}

@Composable
fun RecordScreenContent(

){

}
