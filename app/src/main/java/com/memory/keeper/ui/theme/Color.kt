package com.memory.keeper.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.memory.keeper.R

@Immutable
data class Colors(
    val surface: Color,
    val background: Color,
    val primary: Color,
    val onPrimary: Color,
    val textPrimary: Color,
    val textOnPrimary: Color,
    val textSecondary: Color,
    val textThird: Color,
    val optionBorderUnfocused: Color,
    val optionTextUnfocused: Color,
    val optionUnfocused: Color,
    val optionFocused: Color,
    val buttonBorderUnfocused: Color,
    val buttonUnfocused: Color,
    val badge: Color,
    val badgeText: Color,
    val divider: Color,
    val center: Color,
    val optionBorderFocused: Color,
    val optionTextFocused: Color,
    val buttonTextUnfocused: Color,
    val iconSelected: Color,
    val iconUnSelected: Color,
    val iconDefault: Color,
    val box: Color,
    val buttonText: Color,
    val calendarBg: Color,
    val red: Color,
    val buttonSecondary: Color,
    val prefix: Color,
) {

    companion object {
        @Composable
        fun defaultLightColors(): Colors = Colors(
            surface = colorResource(id = R.color.white_700),
            background = colorResource(id = R.color.white_600),
            primary = colorResource(id = R.color.orange_600),
            onPrimary = colorResource(id = R.color.black_800),
            textPrimary = colorResource(id = R.color.grey_900),
            textOnPrimary = colorResource(id = R.color.white_700),
            textSecondary = colorResource(id = R.color.white_100),
            textThird = colorResource(id = R.color.black_900),
            optionBorderUnfocused = colorResource(id = R.color.grey_400),
            optionTextUnfocused = colorResource(id = R.color.grey_800),
            optionUnfocused = colorResource(id = R.color.grey_100),
            optionFocused = colorResource(id = R.color.orange_300),
            buttonBorderUnfocused = colorResource(id = R.color.white_300),
            buttonUnfocused = colorResource(id = R.color.grey_300),
            badge = colorResource(id = R.color.white_300),
            badgeText = colorResource(id = R.color.white_700),
            divider = colorResource(id = R.color.white_600),
            center = colorResource(id = R.color.white_500),
            optionBorderFocused = colorResource(id = R.color.orange_900),
            optionTextFocused = colorResource(id = R.color.white_700),
            buttonTextUnfocused = colorResource(id = R.color.black_900),
            iconSelected = colorResource(id = R.color.black_700),
            iconUnSelected = colorResource(id = R.color.grey_600),
            iconDefault = colorResource(id = R.color.grey_900),
            box = colorResource(id = R.color.grey_200),
            buttonText = colorResource(id = R.color.white_700),
            calendarBg = colorResource(id = R.color.grey_200),
            red = colorResource(id = R.color.red),
            buttonSecondary = colorResource(id = R.color.orange_300),
            prefix = colorResource(id = R.color.black_400)
        )
    }
}