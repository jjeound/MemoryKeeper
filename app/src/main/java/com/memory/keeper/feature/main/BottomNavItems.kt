package com.memory.keeper.feature.main

import androidx.annotation.StringRes
import com.memory.keeper.R
import com.memory.keeper.navigation.Screen

enum class BottomNavItems(
    @StringRes val label: Int,
    val icon: Int,
    @StringRes val contentDescription: Int,
    val route: Screen
) {
    PROMPT(R.string.prompt, R.drawable.prompt, R.string.prompt, Screen.Prompt),
    HOME(R.string.home, R.drawable.home, R.string.home, Screen.Home),
    MY(R.string.my, R.drawable.my, R.string.my, Screen.My),
}