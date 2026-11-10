package com.arman.dev.physioexamprep.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arman.dev.physioexamprep.R

// ---------------------------------------------------------------------------
// Font: Plus Jakarta Sans (res/font) – same family used in the Figma design.
// ---------------------------------------------------------------------------
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

private fun style(
    size: Int,
    lineHeight: Int,
    weight: FontWeight,
    tracking: Double = 0.0, // Figma letter-spacing in %, e.g. -3 % -> -0.03
) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.em,
)

// ---------------------------------------------------------------------------
// Pulse type scale – named after where each style is used in the Figma screens.
// Use these directly: Text("Anatomy", style = PulseType.SubjectTitle)
// ---------------------------------------------------------------------------
object PulseType {
    // Display / big numbers
    val ScoreDisplay = style(68, 72, FontWeight.ExtraBold, -0.04)   // Result: "164"
    val StatNumber = style(40, 44, FontWeight.ExtraBold, -0.03)     // Home goal: "32"

    // Headlines
    val SubjectTitle = style(36, 40, FontWeight.ExtraBold, -0.03)   // Topic picker: "Anatomy"
    val ScreenTitle = style(30, 36, FontWeight.ExtraBold, -0.03)    // "Learn", "Mock tests", "Profile"
    val HeroTitle = style(26, 32, FontWeight.ExtraBold, -0.02)      // "All-India Mock #4"
    val Question = style(24, 32, FontWeight.ExtraBold, -0.02)       // Quiz question, greeting name
    val SheetTitle = style(20, 26, FontWeight.ExtraBold, -0.01)     // "Choose topics", "Not quite"
    val SectionTitle = style(18, 24, FontWeight.ExtraBold, -0.01)   // "Focus areas", tile titles
    val CardTitle = style(17, 22, FontWeight.ExtraBold, -0.01)      // Subject tile / exam card name
    val ListTitle = style(16, 22, FontWeight.Bold)                   // "Anatomy · Upper Limb", mock rows

    // Body
    val OptionText = style(16, 22, FontWeight.SemiBold)              // Quiz answer options
    val BodyStrong = style(15, 22, FontWeight.Bold)                  // Topic names, list items
    val Body = style(15, 22, FontWeight.Medium)                      // Explanation text
    val BodySmall = style(14, 20, FontWeight.Medium)                 // Subtitles under titles
    val Meta = style(13, 18, FontWeight.Medium)                      // "1,240 Qs · 12 topics"
    val Caption = style(12, 16, FontWeight.Medium)                   // Stat labels, small meta

    // Labels
    val Button = style(16, 20, FontWeight.Bold)                      // Button component label
    val Chip = style(14, 18, FontWeight.SemiBold)                    // Filter chips, links ("See all")
    val Pill = style(12, 16, FontWeight.Bold)
    val BottomNav = style(14,18, FontWeight.Bold)
    // Status pills ("Strong", "Hard")
    val Overline = style(11, 14, FontWeight.Bold, 0.10)              // "TODAY'S GOAL", "SUBJECT"
}

// ---------------------------------------------------------------------------
// Material 3 typography mapped onto the Pulse scale, so MaterialTheme.typography
// also gives the Figma look.
// ---------------------------------------------------------------------------
val Typography = Typography(
    displayLarge = PulseType.ScoreDisplay,
    displayMedium = PulseType.StatNumber,
    displaySmall = PulseType.SubjectTitle,
    headlineLarge = PulseType.ScreenTitle,
    headlineMedium = PulseType.HeroTitle,
    headlineSmall = PulseType.Question,
    titleLarge = PulseType.SheetTitle,
    titleMedium = PulseType.SectionTitle,
    titleSmall = PulseType.ListTitle,
    bodyLarge = PulseType.OptionText,
    bodyMedium = PulseType.Body,
    bodySmall = PulseType.Meta,
    labelLarge = PulseType.Button,
    labelMedium = PulseType.Chip,
    labelSmall = PulseType.Overline,
)
