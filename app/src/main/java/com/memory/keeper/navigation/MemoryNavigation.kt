package com.memory.keeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.memory.keeper.feature.home.HomeScreen
import com.memory.keeper.feature.login.SelectModeScreen
import com.memory.keeper.feature.login.SignUpFinishScreen
import com.memory.keeper.feature.login.SignUpRelationScreen
import com.memory.keeper.feature.login.SignUpScreen
import com.memory.keeper.feature.login.SignUpSearchUserScreen

fun NavGraphBuilder.memoryNavigation() {
    navigation<Graph.SignUpGraph>(
        startDestination = Screen.SignUp
    ){
        composable<Screen.SignUp> {
            SignUpScreen()
        }
        composable<Screen.SelectMode> {
            val args = it.toRoute<Screen.SelectMode>()
            SelectModeScreen(name = args.name)
        }
        composable<Screen.SearchUser> {
            val args = it.toRoute<Screen.SearchUser>()
            SignUpSearchUserScreen(name = args.name)
        }
        composable<Screen.SetRelation> {
            val args = it.toRoute<Screen.SetRelation>()
            SignUpRelationScreen(
                name = args.name,
                userName = args.userName,
                userId = args.userId,
            )
        }
        composable<Screen.SignUpFinish> {
            val args = it.toRoute<Screen.SignUpFinish>()
            SignUpFinishScreen(
                name = args.name,
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