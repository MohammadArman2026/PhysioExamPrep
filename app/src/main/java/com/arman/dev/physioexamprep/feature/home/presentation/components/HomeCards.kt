package com.arman.dev.physioexamprep.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.ProgressRing
import com.arman.dev.physioexamprep.core.ui.components.ProgressTrack
import com.arman.dev.physioexamprep.core.ui.components.PulseButton
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.subject.subjectVisual
import com.arman.dev.physioexamprep.core.ui.theme.Amber
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Sky
import com.arman.dev.physioexamprep.core.ui.theme.SkySoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.ui.theme.White
import com.arman.dev.physioexamprep.feature.home.domain.DailyGoal
import com.arman.dev.physioexamprep.feature.home.domain.FocusArea
import com.arman.dev.physioexamprep.feature.home.domain.PyqOfTheDay
import com.arman.dev.physioexamprep.feature.home.domain.QuickMix
import com.arman.dev.physioexamprep.feature.home.domain.ResumeItem
import com.arman.dev.physioexamprep.feature.home.domain.UpcomingMock
import java.text.NumberFormat
import java.util.Locale

/** "Hero / Daily goal": ink card, 28dp corners, goal numbers + ring + lime CTA. */
@Composable
fun DailyGoalHero(goal: DailyGoal, streakDays: Int, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Ink)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("TODAY'S GOAL", style = PulseType.Overline.copy(color = Lime))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${goal.answered}", style = PulseType.StatNumber.copy(color = White))
                    Text(
                        "/ ${goal.target} Qs",
                        style = PulseType.Body.copy(color = TextTertiary, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                Text(
                    text = if (goal.remaining == 0) "Goal done — your $streakDays‑day streak is safe"
                    else "${goal.remaining} more to keep your $streakDays‑day streak alive",
                    style = PulseType.BodySmall.copy(color = White),
                )
            }
            ProgressRing(fraction = goal.fraction, label = "${(goal.fraction * 100).toInt()}%")
        }
        PulseButton(label = "Continue practice", onClick = onContinue, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ResumeCard(item: ResumeItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val visual = subjectVisual(item.subjectId)
    Row(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(visual.icon, size = 52.dp, iconSize = 24.dp, container = visual.soft, tint = visual.accent, shape = RoundedCornerShape(16.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${item.subjectName} · ${item.topicName}", style = PulseType.ListTitle.copy(color = Ink))
            Text("${item.subtopicName} · ${item.questionsLeft} Qs left", style = PulseType.Meta.copy(color = TextSecondary))
            ProgressTrack(item.progress, visual.accent, Modifier.fillMaxWidth().padding(top = 6.dp))
        }
        IconBadge(R.drawable.ic_chev_right, size = 36.dp, iconSize = 18.dp, container = SurfaceMuted, contentDescription = "Resume")
    }
}

@Composable
fun BentoTiles(
    quickMix: QuickMix,
    pyq: PyqOfTheDay?,
    onQuickMix: () -> Unit,
    onPyq: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BentoTile(
            title = "Quick ${quickMix.questionCount}",
            subtitle = "Random mix · ~${quickMix.approxMinutes} min",
            icon = R.drawable.ic_zap,
            container = Lime,
            badge = Ink,
            iconTint = Lime,
            onClick = onQuickMix,
            modifier = Modifier.weight(1f),
        )
        if (pyq != null) {
            BentoTile(
                title = "PYQ of the day",
                subtitle = "${pyq.exam} ${pyq.year} · Q${pyq.questionNumber}",
                icon = R.drawable.ic_calendar,
                container = SkySoft,
                badge = Sky,
                iconTint = White,
                onClick = onPyq,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BentoTile(
    title: String,
    subtitle: String,
    icon: Int,
    container: Color,
    badge: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .height(168.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(container)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        IconBadge(icon, size = 42.dp, iconSize = 20.dp, container = badge, tint = iconTint)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = PulseType.SectionTitle.copy(color = Ink))
            Text(subtitle, style = PulseType.Meta.copy(color = Ink))
        }
    }
}

/** Below 50 % = red (needs work), otherwise amber. Matches the three Figma rows. */
@Composable
fun FocusAreasCard(areas: List<FocusArea>, onClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(vertical = 10.dp),
    ) {
        areas.forEach { area ->
            val weak = area.accuracyPercent < 50
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) { onClick(area.subjectId) }
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(area.subjectName, style = PulseType.BodyStrong.copy(color = Ink))
                    Text(area.topicName, style = PulseType.Caption.copy(color = TextTertiary))
                }
                ProgressTrack(area.accuracyPercent / 100f, if (weak) Danger else Amber, Modifier.width(72.dp))
                Text(
                    "${area.accuracyPercent}%",
                    style = PulseType.Chip.copy(color = if (weak) Danger else Ink, fontWeight = FontWeight.ExtraBold),
                )
            }
        }
    }
}

@Composable
fun UpcomingMockCard(mock: UpcomingMock, onRegister: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(LilacSoft)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("LIVE · ${mock.startsAtLabel}", style = PulseType.Overline.copy(color = Lilac, letterSpacing = PulseType.Overline.letterSpacing * 0.8f))
            Text(mock.title, style = PulseType.SectionTitle.copy(color = Ink))
            Text(
                "${NumberFormat.getIntegerInstance(Locale.US).format(mock.registeredCount)} aspirants registered",
                style = PulseType.Meta.copy(color = TextSecondary),
            )
        }
        Text(
            text = if (mock.isRegistered) "Registered" else "Register",
            style = PulseType.Chip.copy(color = if (mock.isRegistered) Ink else White, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .clip(CircleShape)
                .background(if (mock.isRegistered) White else Ink)
                .clickable(enabled = !mock.isRegistered, role = Role.Button, onClick = onRegister)
                .padding(horizontal = 18.dp, vertical = 11.dp),
        )
    }
}
