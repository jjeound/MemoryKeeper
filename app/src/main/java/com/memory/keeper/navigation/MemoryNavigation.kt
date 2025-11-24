package com.memory.keeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.memory.keeper.feature.chat.AIChatScreen
import com.memory.keeper.feature.chat.ChatScreen
import com.memory.keeper.feature.chat.VideoScreen
import com.memory.keeper.feature.home.HomeScreen
import com.memory.keeper.feature.login.SelectModeScreen
import com.memory.keeper.feature.login.SignUpFinishScreen
import com.memory.keeper.feature.login.SignUpRelationScreen
import com.memory.keeper.feature.login.SignUpScreen
import com.memory.keeper.feature.login.SignUpSearchUserScreen
import com.memory.keeper.feature.my.MyScreen
import com.memory.keeper.feature.notification.NotificationScreen
import com.memory.keeper.feature.prompt.PromptScreen

fun NavGraphBuilder.memoryNavigation(
) {
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
    navigation<Graph.PromptGraph>(
        startDestination = Screen.Prompt
    ){
        composable<Screen.Prompt> {
            PromptScreen()
        }
    }
    navigation<Graph.HomeGraph>(
        startDestination = Screen.Home
    ){
        composable<Screen.Home> {
            HomeScreen()
        }
    }
    navigation<Graph.ChatGraph>(
        startDestination = Screen.Chat
    ){
        composable<Screen.Chat> {
            ChatScreen()
        }
        composable<Screen.AIChatScreen> {
            AIChatScreen()
        }
        composable<Screen.VideoScreen> {
            VideoScreen()
        }
    }
    navigation<Graph.MyGraph>(
        startDestination = Screen.My
    ){
        composable<Screen.My> {
            MyScreen()
        }
        composable<Screen.SearchUser> {
            val args = it.toRoute<Screen.SearchUser>()
            SignUpSearchUserScreen(
                name = args.name,
                title = "관계 설정하기"
            )
        }
        composable<Screen.SetRelation> {
            val args = it.toRoute<Screen.SetRelation>()
            SignUpRelationScreen(
                name = args.name,
                userName = args.userName,
                userId = args.userId,
                title = "관계 설정하기",
                fromMy = true
            )
        }
    }
    composable<Screen.Notification>{
        NotificationScreen()
    }
}