package com.arman.dev.physioexamprep.feature.topicpicker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.Pill
import com.arman.dev.physioexamprep.core.ui.components.PulseButton
import com.arman.dev.physioexamprep.core.ui.components.PulseButtonStyle
import com.arman.dev.physioexamprep.core.ui.components.SegmentedControl
import com.arman.dev.physioexamprep.core.ui.theme.AmberSoft
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.DangerSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SuccessSoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.feature.topicpicker.domain.Mastery
import com.arman.dev.physioexamprep.feature.topicpicker.domain.TopicProgress
import com.arman.dev.physioexamprep.feature.topicpicker.presentation.TopicPickerIntent

/** Topic row: selected = 2dp ink border + ink checkbox with lime tick. */
@Composable
fun TopicRow(topic: TopicProgress, selected: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardSurface)
            .border(if (selected) 2.dp else 1.dp, if (selected) Ink else Line, shape)
            .clickable(role = Role.Checkbox, onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        val boxShape = RoundedCornerShape(8.dp)
        Box(
            Modifier
                .size(26.dp)
                .clip(boxShape)
                .background(if (selected) Ink else CardSurface)
                .then(if (selected) Modifier else Modifier.border(1.5.dp, Line, boxShape)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(painterResource(R.drawable.ic_check), null, tint = Lime, modifier = Modifier.size(16.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(topic.name, style = PulseType.BodyStrong.copy(color = Ink))
            Text(
                "${topic.questionCount} Qs · ${topic.accuracyPercent?.let { "$it%" } ?: "—"} accuracy",
                style = PulseType.Caption.copy(color = TextTertiary),
            )
        }
        MasteryPill(topic.mastery)
    }
}

@Composable
fun MasteryPill(mastery: Mastery, modifier: Modifier = Modifier) {
    val (bg, fg) = when (mastery) {
        Mastery.STRONG -> SuccessSoft to Success
        Mastery.FAIR -> AmberSoft to Ink
        Mastery.WEAK -> DangerSoft to Danger
        Mastery.NEW -> SurfaceMuted to TextSecondary
    }
    Pill(mastery.label, bg, fg, modifier)
}

/** Fixed white bottom sheet: questions stepper, mode toggle and the start button. */
@Composable
fun PracticeActionSheet(
    questions: Int,
    estimatedMinutes: Int,
    mode: PracticeMode,
    canDecrease: Boolean,
    canIncrease: Boolean,
    canStart: Boolean,
    onIntent: (TopicPickerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(20.dp, shape, ambientColor = Ink.copy(alpha = 0.1f), spotColor = Ink.copy(alpha = 0.1f))
            .clip(shape)
            .background(CardSurface)
            .then(modifier)
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Questions per session", style = PulseType.Chip.copy(color = Ink, fontWeight = FontWeight.Bold))
                val pace = when (mode) {
                    PracticeMode.PRACTICE -> "untimed"
                    PracticeMode.TIMED -> "timed"
                    PracticeMode.EXAM -> "exam conditions"
                }
                Text("~$estimatedMinutes min · $pace", style = PulseType.Caption.copy(color = TextTertiary))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconBadge(
                    R.drawable.ic_minus, size = 38.dp, iconSize = 18.dp, container = SurfaceMuted,
                    tint = if (canDecrease) Ink else TextTertiary, contentDescription = "Fewer questions",
                    onClick = if (canDecrease) ({ onIntent(TopicPickerIntent.DecreaseQuestions) }) else null,
                )
                Text("$questions", style = PulseType.SheetTitle.copy(color = Ink))
                IconBadge(
                    R.drawable.ic_plus, size = 38.dp, iconSize = 18.dp, container = Ink,
                    tint = if (canIncrease) Lime else TextTertiary, contentDescription = "More questions",
                    onClick = if (canIncrease) ({ onIntent(TopicPickerIntent.IncreaseQuestions) }) else null,
                )
            }
        }
        SegmentedControl(
            options = PracticeMode.entries.map { it.label },
            selectedIndex = mode.ordinal,
            onSelect = { onIntent(TopicPickerIntent.ModeSelected(PracticeMode.entries[it])) },
        )
        val verb = when (mode) {
            PracticeMode.PRACTICE -> "Start practice"
            PracticeMode.TIMED -> "Start timed"
            PracticeMode.EXAM -> "Start exam"
        }
        PulseButton(
            label = "$verb · $questions Qs",
            onClick = { onIntent(TopicPickerIntent.StartClicked) },
            style = PulseButtonStyle.Ink,
            enabled = canStart,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
