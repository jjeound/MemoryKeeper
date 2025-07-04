package com.memory.keeper.feature.main

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.memory.keeper.R

enum class BottomNavItems(
    @StringRes val label: Int,
    val icon: Int,
    @StringRes val contentDescription: Int
) {
    HOME(R.string.home, R.drawable.home, R.string.home),
    GOAL(R.string.goal, R.drawable.goal, R.string.goal),
    REPORT(R.string.report, R.drawable.report, R.string.report),
    MY(R.string.my, R.drawable.my, R.string.my),
}