package com.arman.dev.physioexamprep.core.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Pulse palette – matches the "Pulse" colour variables in the Figma file
// (page: "PhysioPrep — Fresh Concept"). Names mirror Figma: pulse/<name>.
// (canvas/surface/surface2 are renamed so they don't clash with Compose's
//  Canvas() and Surface() composables.)
// ---------------------------------------------------------------------------

// Ink – dark surfaces, primary text, nav bar
val Ink = Color(0xFF0E1220)
val Ink2 = Color(0xFF1C2233)          // raised surface on ink (explanation box, stat tiles)
val Ink3 = Color(0xFF2A3147)          // chips / tracks / soft buttons on ink

// Backgrounds & surfaces
val AppCanvas = Color(0xFFF4F3EE)     // pulse/canvas  – app background (warm off-white)
val CardSurface = Color(0xFFFFFFFF)   // pulse/surface – cards
val SurfaceMuted = Color(0xFFECEBE4)  // pulse/surface2 – muted fills, progress tracks, badges
val Line = Color(0xFFE3E1D8)          // borders & dividers

// Text
val TextPrimary = Color(0xFF0E1220)
val TextSecondary = Color(0xFF5B6170)
val TextTertiary = Color(0xFF8C91A0)
val White = Color(0xFFFFFFFF)

// Brand accent – Lime
val Lime = Color(0xFFC8F169)          // primary CTA, active nav tab, progress on ink
val LimeSoft = Color(0xFFEEF8D3)
val LimeDeep = Color(0xFF4D6B00)      // lime-family text/icons on light backgrounds

// Subject colours (each subject keeps its own colour across the app)
val Coral = Color(0xFFFF7759)         // Anatomy
val CoralSoft = Color(0xFFFFE6DE)
val Sky = Color(0xFF3BA7F5)           // Physiology
val SkySoft = Color(0xFFDDEFFF)
val Amber = Color(0xFFF5A623)         // Electrotherapy
val AmberSoft = Color(0xFFFFF0D2)
val Lilac = Color(0xFF8E6CFF)         // Orthopedics
val LilacSoft = Color(0xFFECE6FF)
// Exercise Therapy uses LimeDeep / LimeSoft

// Feedback / status
val Success = Color(0xFF1FA463)       // correct answer, "Strong" topic
val SuccessSoft = Color(0xFFDCF5E7)
val Danger = Color(0xFFE5484D)        // wrong answer, "Weak" topic, log out
val DangerSoft = Color(0xFFFDE3E4)
