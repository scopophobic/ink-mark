package com.example.newdraw.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InkPrimary,
    onPrimary = InkSurface,
    surface = InkSurface,
    onSurface = InkBlack,
    onSurfaceVariant = InkBlack,
    background = InkSurface,
    onBackground = InkBlack,
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onError = androidx.compose.ui.graphics.Color.White
).copy(
    // Override all text colors to be black
    onSurface = InkBlack,
    onBackground = InkBlack,
    onPrimary = InkBlack
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