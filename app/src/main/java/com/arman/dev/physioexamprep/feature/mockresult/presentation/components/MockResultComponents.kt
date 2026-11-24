package com.arman.dev.physioexamprep.feature.mockresult.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.ProgressTrack
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.subject.subjectVisual
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.DangerSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Ink2
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SuccessSoft
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.ui.theme.White
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResult
import com.arman.dev.physioexamprep.feature.mockresult.domain.SubjectBreakdown
import java.text.NumberFormat
import java.util.Locale

/** Centre of the ink hero: YOUR SCORE, big number, rank pill, three stat tiles. */
@Composable
fun ScoreHeroContent(result: MockResult) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("YOUR SCORE", style = PulseType.Overline.copy(color = TextTertiary), modifier = Modifier.padding(top = 28.dp))
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 10.dp)) {
            Text("${result.score}", style = PulseType.ScoreDisplay.copy(color = White, fontSize = 72.sp, lineHeight = 86.sp))
            Text(
                "/${result.maxScore}",
                style = PulseType.Question.copy(color = TextTertiary, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp, bottom = 14.dp),
            )
        }
        result.ranking?.let { r ->
            Row(
                Modifier
                    .padding(top = 10.dp)
                    .clip(CircleShape)
                    .background(Lime)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(painterResource(R.drawable.ic_award), null, tint = Ink, modifier = Modifier.size(16.dp))
                Text(
                    "Top ${r.topPercent}% · Rank ${r.rank} of ${NumberFormat.getIntegerInstance(Locale.US).format(r.candidates)}",
                    style = PulseType.Meta.copy(color = Ink, fontWeight = FontWeight.Bold),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val mins = result.timeTakenSeconds / 60
            HeroStat("Accuracy", "${result.accuracyPercent}%", Modifier.weight(1f))
            HeroStat("Time", if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "${mins}m ${result.timeTakenSeconds % 60}s", Modifier.weight(1f))
            HeroStat("Attempted", "${result.attempted}/${result.totalQuestions}", Modifier.weight(1f))
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Ink2)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, style = PulseType.Caption.copy(color = TextTertiary))
        Text(value, style = PulseType.SheetTitle.copy(color = White), maxLines = 1)
    }
}

@Composable
fun SubjectBreakdownCard(subjects: List<SubjectBreakdown>, showDelta: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Subject breakdown", style = PulseType.CardTitle.copy(color = Ink), modifier = Modifier.weight(1f))
            if (showDelta) Text("vs. avg", style = PulseType.Meta.copy(color = TextTertiary))
        }
        subjects.forEach { s ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(s.name, style = PulseType.Chip.copy(color = Ink), modifier = Modifier.weight(1f))
                    s.deltaVsAverage?.takeIf { showDelta }?.let { d ->
                        Text(
                            if (d >= 0) "+$d" else "−${-d}",
                            style = PulseType.Pill.copy(color = if (d >= 0) Success else Danger),
                        )
                    }
                    Text("${s.percent}%", style = PulseType.Chip.copy(color = Ink, fontWeight = FontWeight.ExtraBold))
                }
                ProgressTrack(s.percent / 100f, subjectVisual(s.subjectId).accent, Modifier.fillMaxWidth(), height = 8.dp)
            }
        }
    }
}

@Composable
fun InsightTiles(result: MockResult, modifier: Modifier = Modifier) {
    val strongest = result.strongest ?: return
    val weakest = result.weakest
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        InsightTile("Strongest", strongest, R.drawable.ic_trend, Success, SuccessSoft, Modifier.weight(1f))
        if (weakest != null) InsightTile("Needs work", weakest, R.drawable.ic_alert, Danger, DangerSoft, Modifier.weight(1f))
    }
}

@Composable
private fun InsightTile(label: String, s: SubjectBreakdown, icon: Int, accent: Color, soft: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .height(132.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(soft)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        IconBadge(icon, size = 36.dp, iconSize = 18.dp, container = CardSurface, tint = accent, shape = RoundedCornerShape(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = PulseType.Caption.copy(color = TextSecondary))
            Text(s.name, style = PulseType.ListTitle.copy(color = Ink), maxLines = 1)
            Text("${s.percent}% · ${s.correct} of ${s.total}", style = PulseType.Caption.copy(color = TextSecondary))
        }
    }
}
