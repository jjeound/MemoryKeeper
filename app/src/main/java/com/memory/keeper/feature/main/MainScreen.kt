package com.memory.keeper.feature.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.memory.keeper.feature.login.SignUpScreen
import com.memory.keeper.navigation.AppComposeNavigator
import com.memory.keeper.navigation.Screen
import com.memory.keeper.ui.theme.MemoryTheme

@Composable
fun MainScreen(composeNavigator: AppComposeNavigator<Screen>) {
    MemoryTheme {
        val navHostController = rememberNavController()
        val adaptiveInfo = currentWindowAdaptiveInfo()
        val customNavSuiteType = with(adaptiveInfo) {
            if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
            }
        }
        val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = MemoryTheme.colors.primary,
                selectedTextColor = MemoryTheme.colors.primary,
                unselectedIconColor = MemoryTheme.colors.iconDefault,
                unselectedTextColor = MemoryTheme.colors.iconDefault,
                indicatorColor = Color.Transparent
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                selectedIconColor = MemoryTheme.colors.primary,
                selectedTextColor = MemoryTheme.colors.primary,
                unselectedIconColor = MemoryTheme.colors.iconDefault,
                unselectedTextColor = MemoryTheme.colors.iconDefault,
                indicatorColor = Color.Transparent
            )
        )
        var currentDestination by rememberSaveable { mutableStateOf(BottomNavItems.HOME) }
        LaunchedEffect(Unit) {
            composeNavigator.handleNavigationCommands(navHostController)
        }

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                BottomNavItems.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(it.icon),
                                contentDescription = stringResource(it.contentDescription)
                            )
                        },
                        label = { Text(stringResource(it.label)) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it },
                        colors = myNavigationSuiteItemColors
                    )
                }
            },
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = MemoryTheme.colors.box,
                navigationBarContentColor = MemoryTheme.colors.textPrimary,
                navigationRailContainerColor = MemoryTheme.colors.box,
                navigationRailContentColor = MemoryTheme.colors.textPrimary,
            ),
            containerColor = MemoryTheme.colors.surface,
            contentColor = MemoryTheme.colors.surface,
            layoutType = customNavSuiteType
        ) {
            when(currentDestination){
                BottomNavItems.HOME -> {
                    SignUpScreen()
                }
                BottomNavItems.GOAL -> {

                }
                BottomNavItems.MY -> {

                }
                BottomNavItems.REPORT -> {

                }
            }
            //MemoryNavHost(navHostController = navHostController)
        }
    }
}