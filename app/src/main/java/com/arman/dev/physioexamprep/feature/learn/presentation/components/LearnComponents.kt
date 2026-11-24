package com.arman.dev.physioexamprep.feature.learn.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.ProgressTrack
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.subject.subjectVisual
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.learn.domain.LearnOverview
import com.arman.dev.physioexamprep.feature.learn.domain.SubjectSummary
import java.text.NumberFormat
import java.util.Locale

/**
 * "Overall mastery" card. The segmented bar has one coloured segment per subject, sized by how
 * many of the total questions that subject has mastered; the grey tail is what's left.
 */
@Composable
fun OverallMasteryCard(overview: LearnOverview, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Overall mastery", style = PulseType.Chip.copy(color = TextSecondary), modifier = Modifier.weight(1f))
            if (overview.weeklyChangePercent != 0) {
                Icon(painterResource(R.drawable.ic_trend), null, tint = Success, modifier = Modifier.size(16.dp))
                Text(
                    "${if (overview.weeklyChangePercent > 0) "+" else ""}${overview.weeklyChangePercent}% this week",
                    style = PulseType.Meta.copy(color = Success, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
        Text("${overview.overallMasteryPercent}%", style = PulseType.ScreenTitle.copy(color = Ink))
        val total = overview.totalQuestions.coerceAtLeast(1).toFloat()
        val weights = overview.subjects.map { it.id to it.questionCount * it.masteryPercent / 100f / total }
        val rest = (1f - weights.sumOf { it.second.toDouble() }.toFloat()).coerceAtLeast(0f)
        Row(Modifier.fillMaxWidth().height(10.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            weights.filter { it.second > 0f }.forEach { (id, w) ->
                Box(
                    Modifier
                        .weight(w)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(subjectVisual(id).accent)
                )
            }
            if (rest > 0f) {
                Box(Modifier.weight(rest).height(10.dp).clip(RoundedCornerShape(5.dp)).background(SurfaceMuted))
            }
        }
    }
}

/** 192dp subject tile in the soft subject colour. */
@Composable
fun SubjectTile(subject: SubjectSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val visual = subjectVisual(subject.id)
    val count = NumberFormat.getIntegerInstance(Locale.US).format(subject.questionCount)
    Column(
        modifier
            .fillMaxWidth()
            .height(192.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(visual.soft)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconBadge(visual.icon, size = 44.dp, iconSize = 22.dp, container = CardSurface, tint = visual.accent)
            Text(
                text = if (subject.isNew) "New" else "${subject.masteryPercent}%",
                style = PulseType.Meta.copy(color = visual.accent, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(subject.name, style = PulseType.CardTitle.copy(color = Ink))
            Text("$count Qs · ${subject.topicCount} topics", style = PulseType.Caption.copy(color = TextSecondary))
            ProgressTrack(
                fraction = if (subject.isNew) 0.008f else subject.masteryPercent / 100f,
                color = visual.accent,
                track = CardSurface,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}
