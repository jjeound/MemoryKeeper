package com.memory.keeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.memory.keeper.feature.home.HomeScreen
import com.memory.keeper.feature.login.SelectModeScreen
import com.memory.keeper.feature.login.SignUpRelationScreen
import com.memory.keeper.feature.login.SignUpScreen
import com.memory.keeper.feature.login.SignUpSetNameScreen

fun NavGraphBuilder.memoryNavigation() {
    navigation<Graph.SignUpGraph>(
        startDestination = Screen.SignUp
    ){
        composable<Screen.SignUp> {
            SignUpScreen()
        }
        composable<Screen.SelectMode> {
            SelectModeScreen()
        }
        composable<Screen.SetName> {
            SignUpSetNameScreen()
        }
        composable<Screen.SetRelation> {
            val args = it.toRoute<Screen.SetRelation>()
            SignUpRelationScreen(
                name = args.name,
                userName = args.userName
            )
        }
    }
    navigation<Graph.HomeGraph>(
        startDestination = Screen.Home
    ){
        composable<Screen.Home> {
            HomeScreen()
        }
    }
    navigation<Graph.ReportGraph>(
        startDestination = Screen.Report
    ){
        composable<Screen.Report> {
            HomeScreen()
        }
    }
    navigation<Graph.MyGraph>(
        startDestination = Screen.My
    ){
        composable<Screen.My> {
            HomeScreen()
        }
    }
}