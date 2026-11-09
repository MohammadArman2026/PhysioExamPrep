package com.arman.dev.physioexamprep.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val customColorScheme = lightColorScheme(
    primary = iconColor,
    secondary = iconBackGround,
    tertiary = descriptionColor,

    background = appBackGround,
    surface = cardColor,

    onPrimary = cardColor,
    onSecondary = titleColor,
    onTertiary = cardColor,

    onBackground = titleColor,
    onSurface = titleColor
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