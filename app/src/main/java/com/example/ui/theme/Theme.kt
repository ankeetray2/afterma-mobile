package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = Color.White,
    primaryContainer = SoftPink,
    onPrimaryContainer = DeepPink,
    secondary = CalmBlue,
    onSecondary = PrimaryText,
    secondaryContainer = CalmBlue,
    onSecondaryContainer = PrimaryText,
    tertiary = HealingGreen,
    onTertiary = PrimaryText,
    background = Background,
    onBackground = PrimaryText,
    surface = SurfaceWhite,
    onSurface = PrimaryText,
    surfaceVariant = CardSurface,
    onSurfaceVariant = PrimaryText,
    outline = Border,
    outlineVariant = DividerColor,
    error = ErrorColor,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = GradientPink,
    onPrimary = Color.Black,
    primaryContainer = DeepPink,
    onPrimaryContainer = SoftPink,
    secondary = CalmBlue,
    onSecondary = Color.Black,
    tertiary = HealingGreen,
    onTertiary = Color.Black,
    background = Color(0xFF1F1218), // Rich dark slate with pink undertones for eye comfort
    onBackground = Color(0xFFFCF6F8),
    surface = Color(0xFF2B1B22),
    onSurface = Color(0xFFFCF6F8),
    surfaceVariant = Color(0xFF332029),
    onSurfaceVariant = Color(0xFFFCF6F8),
    outline = Color(0xFF4A343E),
    outlineVariant = Color(0xFF3D2A33),
    error = ErrorColor,
    onError = Color.Black
)

@Composable
fun AftermaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AftermaTypography,
        shapes = AftermaShapes,
        content = content
    )
}
