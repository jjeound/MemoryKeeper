package com.memory.keeper.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.window.core.layout.WindowSizeClass
import com.memory.keeper.R

private val LocalColors = compositionLocalOf<Colors> {
    error("No colors provided!")
}

private val LocalCustomTypography = compositionLocalOf<CustomTypography> {
    error("No typography provided!")
}

private val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("No window size class provided!")
}

val fontFamily = FontFamily(
    Font(R.font.pretendard_black, FontWeight.Black),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
)

@Composable
fun MemoryTheme(
    colors: Colors = Colors.defaultLightColors(),
    background : Background = Background.defaultBackground(),
    customTypography: CustomTypography = CustomTypography.defaultCustomTypography(fontFamily),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalBackgroundTheme provides background,
        LocalCustomTypography provides customTypography,
        LocalWindowSizeClass provides windowSizeClass
    ) {
        Box(
            modifier = Modifier
                .background(background.color)
                .semantics { testTagsAsResourceId = true },
        ) {
            content()
        }
    }
}

object MemoryTheme {

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val background: Background
        @Composable
        @ReadOnlyComposable
        get() = LocalBackgroundTheme.current

    val typography: CustomTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomTypography.current

    val windowSizeClass: WindowSizeClass
        @Composable
        @ReadOnlyComposable
        get() = LocalWindowSizeClass.current
}