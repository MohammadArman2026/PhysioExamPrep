package com.arman.dev.physioexamprep.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


// Pulse colour scheme (Figma "PhysioPrep — Fresh Concept").
// Ink is the primary colour (dark buttons, nav bar), Lime is the accent (main CTA).
private val customColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = White,
    primaryContainer = Ink2,
    onPrimaryContainer = White,

    secondary = Lime,
    onSecondary = Ink,
    secondaryContainer = LimeSoft,
    onSecondaryContainer = LimeDeep,

    tertiary = Coral,
    onTertiary = White,
    tertiaryContainer = CoralSoft,
    onTertiaryContainer = Ink,

    background = AppCanvas,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,

    outline = Line,
    outlineVariant = Line,

    error = Danger,
    onError = White,
    errorContainer = DangerSoft,
    onErrorContainer = Danger,
)


@Composable
fun PhysioExamPrepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
   // dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> customColorScheme
        else -> customColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}