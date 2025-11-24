package com.memory.keeper.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.memory.keeper.R

@Immutable
data class Background(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
) {
    companion object {
        @Composable
        fun defaultBackground(): Background {
            return Background(
                color = colorResource(id = R.color.white_600),
                tonalElevation = 0.dp,
            )
        }
    }
}

val LocalBackgroundTheme: ProvidableCompositionLocal<Background> =
    staticCompositionLocalOf { Background() }