package com.xiaojinzi.tally.base.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.xiaojinzi.module.base.theme.*

private val LightThemeColors = lightColors(
    primary = Blue500,
    primaryVariant = Blue900,
    secondary = Green400,
    secondaryVariant = Green500,
    background = White50,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    error = Red200,
)

private val DarkThemeColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    secondary = Red300,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    error = Red200,
    onBackground = Color.White
)

@ExperimentalFoundationApi
@Composable
fun TallyTheme(
    // darkTheme: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = TallyTypography,
        shapes = TallyShapes,
    ) {
        CompositionLocalProvider(
            LocalOverScrollConfiguration provides null,
            content = content
        )
    }
}
