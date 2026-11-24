package com.arman.dev.physioexamprep.feature.pyq.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.ProgressTrack
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.theme.Amber
import com.arman.dev.physioexamprep.core.ui.theme.AmberSoft
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.LimeDeep
import com.arman.dev.physioexamprep.core.ui.theme.LimeSoft
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Sky
import com.arman.dev.physioexamprep.core.ui.theme.SkySoft
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SuccessSoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.feature.pyq.domain.ExamPapers
import com.arman.dev.physioexamprep.feature.pyq.domain.ExamTone

private fun ExamTone.colors(): Pair<Color, Color> = when (this) {
    ExamTone.INK -> Ink to Lime
    ExamTone.CORAL -> CoralSoft to Coral
    ExamTone.SKY -> SkySoft to Sky
    ExamTone.AMBER -> AmberSoft to Ink
    ExamTone.LILAC -> LilacSoft to Lilac
    ExamTone.LIME -> LimeSoft to LimeDeep
}

@Composable
fun PyqSearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(CardSurface)
            .border(1.dp, Line, shape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(painterResource(R.drawable.ic_search), null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = PulseType.Body.copy(color = Ink),
            cursorBrush = SolidColor(Ink),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) Text("Search exams, years or topics", style = PulseType.Body.copy(color = TextTertiary))
                    inner()
                }
            },
        )
    }
}

/** Exam card: monogram, name, first four years (solved = green tick), "+N", solved progress. */
@Composable
fun ExamCard(
    exam: ExamPapers,
    onOpen: () -> Unit,
    onPaper: (String) -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (monoBg, monoFg) = exam.tone.colors()
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(monoBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(exam.monogram, style = PulseType.CardTitle.copy(color = monoFg, letterSpacing = PulseType.Body.letterSpacing))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(exam.name, style = PulseType.CardTitle.copy(color = Ink))
                Text(exam.description, style = PulseType.Meta.copy(color = TextSecondary))
            }
            IconBadge(R.drawable.ic_chev_right, size = 36.dp, iconSize = 18.dp, container = SurfaceMuted, contentDescription = "Open ${exam.name}", onClick = onOpen)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            exam.papers.take(4).forEach { paper ->
                Row(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (paper.isSolved) SuccessSoft else SurfaceMuted)
                        .clickable(role = Role.Button) { onPaper(paper.id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (paper.isSolved) Icon(painterResource(R.drawable.ic_check), "Solved", tint = Success, modifier = Modifier.size(13.dp))
                    Text(
                        "${paper.year}",
                        style = PulseType.Meta.copy(color = if (paper.isSolved) Success else TextSecondary, fontWeight = FontWeight.Bold),
                    )
                }
            }
            val more = exam.papers.size - 4
            if (more > 0) {
                Text(
                    "+$more",
                    style = PulseType.Meta.copy(color = TextSecondary, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, Line, CircleShape)
                        .clickable(role = Role.Button, onClick = onMore)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "${exam.solvedCount} of ${exam.papers.size} papers solved",
                style = PulseType.Caption.copy(color = TextSecondary, fontWeight = FontWeight.SemiBold),
            )
            ProgressTrack(
                fraction = if (exam.papers.isEmpty()) 0f else exam.solvedCount.toFloat() / exam.papers.size,
                color = Ink,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Simple row used by the "By topic" and "By year" tabs (not in the Figma file). */
@Composable
fun PyqListRow(title: String, subtitle: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .pulseCard(radius = 20.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = PulseType.CardTitle.copy(color = Ink))
            Text(subtitle, style = PulseType.Meta.copy(color = TextSecondary))
        }
        IconBadge(R.drawable.ic_chev_right, size = 36.dp, iconSize = 18.dp, container = SurfaceMuted)
    }
}
