package com.memory.keeper.feature.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.memory.keeper.navigation.ComposeNavigator
import com.memory.keeper.navigation.LocalComposeNavigator
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun PreviewTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalComposeNavigator provides ComposeNavigator(),
    ) {
        MemoryTheme {
            content()
        }
    }
}