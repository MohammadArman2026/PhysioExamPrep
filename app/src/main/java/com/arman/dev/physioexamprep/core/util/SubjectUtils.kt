package com.arman.dev.physioexamprep.core.util

import androidx.compose.ui.graphics.Color
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.Amber
import com.arman.dev.physioexamprep.core.ui.theme.AmberSoft
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.LimeSoft
import com.arman.dev.physioexamprep.core.ui.theme.Sky
import com.arman.dev.physioexamprep.core.ui.theme.SkySoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextPrimary
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary

data class Subject(
    val name: String,
    val id:Int,
    val totalQuestion:Int,
    val totalTopics:Int,
    val bg: Color,
    val iconBg: Color = CardSurface,
    val iconColor: Color,
    val icon: Int,
    val primaryTextColor: Color = TextPrimary,
    val secondaryTextColor : Color = TextSecondary
)

fun provideSubjects(): List<Subject> {
    return listOf(
        Subject(
            name = "Anatomy",
            id = 1,
            totalQuestion = 100,
            totalTopics = 6,
            bg = CoralSoft,
            iconColor = Coral,
            icon = R.drawable.physiologyanatomy
        ),
        Subject(
            name = "Physiology",
            id = 2,
            totalQuestion = 100,
            totalTopics = 6,
            bg = SkySoft,
            iconColor = Sky,
            icon = R.drawable.cardiopulmonary
        ),
        Subject(
            name = "Exercise Therapy",
            id = 3,
            totalQuestion = 100,
            totalTopics = 6,
            bg = LimeSoft,
            iconColor = Lime,
            icon = R.drawable.exercisetherapy
        ),
        Subject(
            name = "Electrotherapy",
            id = 4,
            totalQuestion = 100,
            totalTopics = 6,
            bg = AmberSoft,
            iconColor = Amber,
            icon = R.drawable.electrotherapy
        ),
        Subject(
            name = "Orthopedics",
            id = 5,
            totalQuestion = 100,
            totalTopics = 6,
            bg = LilacSoft,
            iconColor = Lilac,
            icon = R.drawable.orthopedics
        ),
        Subject(
            name = "Neurology",
            id = 6,
            totalQuestion = 100,
            totalTopics = 6,
            bg = SurfaceMuted,
            iconColor = Ink,
            icon = R.drawable.neuroscience
        )
        ,
        Subject(
            name = "Physiology",
            id = 7,
            totalQuestion = 100,
            totalTopics = 6,
            bg = SkySoft,
            iconColor = Sky,
            icon = R.drawable.cardiopulmonary
        ),
        Subject(
            name = "Exercise Therapy",
            id = 8,
            totalQuestion = 100,
            totalTopics = 6,
            bg = LimeSoft,
            iconColor = Lime,
            icon = R.drawable.exercisetherapy
        )
    )
}