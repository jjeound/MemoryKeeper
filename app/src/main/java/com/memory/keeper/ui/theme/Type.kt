package com.memory.keeper.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class CustomTypography(
    val appBarTitle: TextStyle,
    val option: TextStyle,
    val headlineSmall: TextStyle,
    val header: TextStyle,
    val headlineLarge: TextStyle,
    val more: TextStyle,
    val viewAll: TextStyle,
    val percent: TextStyle,
    val button: TextStyle,
    val counts: TextStyle,
    val body: TextStyle,
    val keywordLarge: TextStyle,
    val keywordMedium: TextStyle,
    val keywordSmall: TextStyle,
    val time: TextStyle,
    val pager: TextStyle,
    val badge: TextStyle,
    val media: TextStyle,
    val description: TextStyle,
    val menu: TextStyle,
){
    companion object {
        @Composable
        fun defaultCustomTypography(fontFamily: FontFamily): CustomTypography = CustomTypography(
            appBarTitle = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
            option = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
            headlineSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
            header = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
            headlineLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 30.sp),
            viewAll = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
            more = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 12.sp),
            percent = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 8.sp),
            button = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 14.sp),
            counts = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp),
            body = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
            keywordLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
            keywordMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp),
            keywordSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 10.sp),
            time = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 10.sp),
            pager = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
            badge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp),
            media = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
            description = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
            menu = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        )
    }
}