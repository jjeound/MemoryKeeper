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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.memory.keeper.navigation.AppComposeNavigator
import com.memory.keeper.navigation.MemoryNavHost
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
                unselectedIconColor = MemoryTheme.colors.iconUnSelected,
                unselectedTextColor = MemoryTheme.colors.iconUnSelected,
                indicatorColor = Color.Transparent
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                selectedIconColor = MemoryTheme.colors.primary,
                selectedTextColor = MemoryTheme.colors.primary,
                unselectedIconColor = MemoryTheme.colors.iconUnSelected,
                unselectedTextColor = MemoryTheme.colors.iconUnSelected,
                indicatorColor = Color.Transparent
            )
        )
        LaunchedEffect(Unit) {
            composeNavigator.handleNavigationCommands(navHostController)
        }
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val route = navBackStackEntry?.destination?.route
        val bottomRoute = route?.split(".")?.lastOrNull()
        val bottomBarDestination = BottomNavItems.entries.any { bottomRoute.equals(it.route.toString()) }

        if (bottomBarDestination) {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    BottomNavItems.entries.forEach { destination ->
                        item(
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(destination.icon),
                                    contentDescription = stringResource(destination.contentDescription)
                                )
                            },
                            label = { Text(stringResource(destination.label)) },
                            selected = navBackStackEntry?.destination?.hierarchy?.any {
                                it.hasRoute(destination.route::class)
                            } == true,
                            onClick = {
                                navHostController.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navHostController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            },
                            alwaysShowLabel = true,
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
                layoutType = customNavSuiteType,
            ) {
                MemoryNavHost(
                    navHostController = navHostController,
                )
            }
        } else {
            MemoryNavHost(
                navHostController = navHostController,
            )
        }
    }
}