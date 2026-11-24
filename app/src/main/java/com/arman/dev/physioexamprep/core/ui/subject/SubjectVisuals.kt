package com.arman.dev.physioexamprep.core.ui.subject

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.core.ui.theme.Amber
import com.arman.dev.physioexamprep.core.ui.theme.AmberSoft
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
import com.arman.dev.physioexamprep.core.ui.theme.LimeDeep
import com.arman.dev.physioexamprep.core.ui.theme.LimeSoft
import com.arman.dev.physioexamprep.core.ui.theme.Sky
import com.arman.dev.physioexamprep.core.ui.theme.SkySoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted

/**
 * "Subject-coded colour" from the Figma cover: each subject keeps one accent + soft tint
 * everywhere (Learn tiles, topic picker header, quiz pill, result bars).
 */
data class SubjectVisual(
    val accent: Color,
    val soft: Color,
    @DrawableRes val icon: Int,
)

fun subjectVisual(subjectId: Long): SubjectVisual = when (subjectId) {
    SubjectIds.ANATOMY -> SubjectVisual(Coral, CoralSoft, R.drawable.ic_user)
    SubjectIds.PHYSIOLOGY -> SubjectVisual(Sky, SkySoft, R.drawable.ic_heart)
    SubjectIds.EXERCISE_THERAPY -> SubjectVisual(LimeDeep, LimeSoft, R.drawable.ic_activity)
    SubjectIds.ELECTROTHERAPY -> SubjectVisual(Amber, AmberSoft, R.drawable.ic_zap)
    SubjectIds.ORTHOPEDICS -> SubjectVisual(Lilac, LilacSoft, R.drawable.ic_bone)
    else -> SubjectVisual(Ink, SurfaceMuted, R.drawable.ic_share) // Neurology + anything new
}
