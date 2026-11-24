package com.arman.dev.physioexamprep.feature.quiz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.HeaderIconButton
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.Pill
import com.arman.dev.physioexamprep.core.ui.components.PulseButton
import com.arman.dev.physioexamprep.core.ui.components.PulseButtonStyle
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Ink2
import com.arman.dev.physioexamprep.core.ui.theme.Ink3
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.ui.theme.White
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizQuestion
import com.arman.dev.physioexamprep.feature.quiz.presentation.QuizState

/** Close button, one segment per question, timer chip. */
@Composable
fun QuizBar(
    total: Int,
    segment: (Int) -> QuizState.Segment,
    currentColor: Color,
    timerLabel: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeaderIconButton(R.drawable.ic_x, "Close", onClick = onClose, size = 44.dp)
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(total) { i ->
                val color = when (segment(i)) {
                    QuizState.Segment.Done -> Ink
                    QuizState.Segment.Current -> currentColor
                    QuizState.Segment.Upcoming -> Line
                }
                Box(Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(color))
            }
        }
        Row(
            Modifier
                .clip(CircleShape)
                .background(Ink)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(painterResource(R.drawable.ic_clock), null, tint = Lime, modifier = Modifier.size(16.dp))
            Text(timerLabel, style = PulseType.Chip.copy(color = White, fontWeight = FontWeight.Bold))
        }
    }
}

/** Flag + bookmark circles and the primary action. */
@Composable
fun QuizActions(
    isBookmarked: Boolean,
    isFlagged: Boolean,
    primaryLabel: String,
    primaryEnabled: Boolean,
    onFlag: () -> Unit,
    onBookmark: () -> Unit,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(AppCanvas)
            .then(modifier)
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 34.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            R.drawable.ic_flag, size = 56.dp, iconSize = 20.dp,
            container = if (isFlagged) Ink else CardSurface, tint = if (isFlagged) Lime else Ink,
            borderColor = if (isFlagged) null else Line,
            contentDescription = if (isFlagged) "Remove flag" else "Flag question", onClick = onFlag,
        )
        IconBadge(
            R.drawable.ic_bookmark, size = 56.dp, iconSize = 20.dp,
            container = if (isBookmarked) Ink else CardSurface, tint = if (isBookmarked) Lime else Ink,
            borderColor = if (isBookmarked) null else Line,
            contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark question", onClick = onBookmark,
        )
        PulseButton(primaryLabel, onPrimary, Modifier.weight(1f), showIcon = false, enabled = primaryEnabled)
    }
}

/** Ink sheet shown after "Check answer" (Figma "Explanation sheet"). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExplanationSheet(
    question: QuizQuestion,
    selectedOptionId: Long?,
    isCorrect: Boolean,
    isLast: Boolean,
    onExplainMore: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val picked = question.options.firstOrNull { it.id == selectedOptionId }?.text
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Ink)
            .then(modifier)
            .padding(start = 22.dp, end = 22.dp, top = 26.dp, bottom = 34.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBadge(
                if (isCorrect) R.drawable.ic_check else R.drawable.ic_x,
                size = 40.dp, iconSize = 20.dp,
                container = if (isCorrect) Success else Danger, tint = White,
                shape = RoundedCornerShape(14.dp),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(if (isCorrect) "Nice, that's right" else "Not quite", style = PulseType.SheetTitle.copy(color = White))
                if (picked != null) Text("You picked $picked", style = PulseType.Meta.copy(color = TextTertiary))
            }
            Pill("${question.communityCorrectPercent}% got it right", Ink3, White, horizontalPadding = 12.dp, verticalPadding = 7.dp)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Ink2)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(painterResource(R.drawable.ic_bulb), null, tint = Lime, modifier = Modifier.size(22.dp))
            Text(question.explanation, style = PulseType.Body.copy(color = White), modifier = Modifier.weight(1f))
        }
        if (question.tags.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.tags.forEachIndexed { i, tag ->
                    Pill(tag, Ink3, if (i == 0) Lime else White, horizontalPadding = 12.dp, verticalPadding = 7.dp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PulseButton("Explain more", onExplainMore, Modifier.weight(1f), style = PulseButtonStyle.Soft, showIcon = false)
            PulseButton(if (isLast) "See results" else "Next", onNext, Modifier.weight(1f))
        }
    }
}
