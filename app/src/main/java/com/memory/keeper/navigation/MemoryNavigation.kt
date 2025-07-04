package com.memory.keeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.memory.keeper.feature.detail.DetailScreen
import com.memory.keeper.feature.home.HomeScreen
import com.memory.keeper.feature.more.MoreScreen
import com.memory.keeper.feature.setting.SettingDetailScreen
import com.memory.keeper.feature.setting.SettingScreen

fun NavGraphBuilder.memoryNavigation() {
    composable<Screen.Home> {
        HomeScreen()
    }

    composable<Screen.More>{
        val args = it.toRoute<Screen.More>()
        MoreScreen(
            isHot = args.isHot
        )
    }
    composable<Screen.Detail>{
        DetailScreen()
    }
    composable<Screen.Setting>{
        SettingScreen()
    }
    composable<Screen.SettingDetail> {
        val args = it.toRoute<Screen.SettingDetail>()
        SettingDetailScreen(args.order)
    }
}