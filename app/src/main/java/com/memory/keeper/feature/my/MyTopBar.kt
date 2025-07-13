package com.memory.keeper.feature.my

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.memory.keeper.ui.theme.MemoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "My",
                color = MemoryTheme.colors.textPrimary,
                style = MemoryTheme.typography.appBarTitle
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MemoryTheme.colors.surface,
        ),
    )
}