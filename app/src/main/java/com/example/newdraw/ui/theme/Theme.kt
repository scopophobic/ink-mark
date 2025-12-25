package com.example.newdraw.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InkPrimary,
    onPrimary = InkSurface,
    surface = InkSurface,
    onSurface = InkBlack,
    background = InkSurface,
    onBackground = InkBlack
)

@Composable
fun NewdrawTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}