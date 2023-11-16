package com.mr_17.sudoku.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val lightColorPalette = lightColorScheme(
    primary = primaryGreen,
    tertiary = textColorLight,
    surface = lightGrey,
    secondary = gridLineColorLight,
    onPrimary = accentAmber,
    onSurface = accentAmber
)

private val darkColorPalette = darkColorScheme(
    primary = primaryCharcoal,
    tertiary = textColorDark,
    surface = lightGreyAlpha,
    secondary = gridLineColorLight,
    onPrimary = accentAmber,
    onSurface = accentAmber
)

@Composable
fun SudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if(darkTheme) darkColorPalette else lightColorPalette,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
