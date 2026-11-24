package com.arman.dev.physioexamprep.feature.mocks.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.Pill
import com.arman.dev.physioexamprep.core.ui.components.PulseButton
import com.arman.dev.physioexamprep.core.ui.components.PulseButtonStyle
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.theme.AmberSoft
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.DangerSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Ink2
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.LimeDeep
import com.arman.dev.physioexamprep.core.ui.theme.LimeSoft
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SuccessSoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.ui.theme.White
import com.arman.dev.physioexamprep.feature.mocks.domain.Difficulty
import com.arman.dev.physioexamprep.feature.mocks.domain.FullLengthMock
import com.arman.dev.physioexamprep.feature.mocks.domain.LiveMock
import com.arman.dev.physioexamprep.feature.mocks.domain.MockStatus
import com.arman.dev.physioexamprep.feature.mocks.presentation.Countdown
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Ink hero: LIVE badge, title, date + joined, 4-tile countdown, reserve button. */
@Composable
fun LiveMockHero(mock: LiveMock, countdown: Countdown, onReserve: () -> Unit, modifier: Modifier = Modifier) {
    val date = Date(mock.startsAtMillis)
    val day = SimpleDateFormat("EEEE", Locale.US).format(date).uppercase(Locale.US)
    val dateLabel = SimpleDateFormat("EEE, d MMM · h a", Locale.US).format(date)
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Ink)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier
                .clip(CircleShape)
                .background(Lime)
                .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(Danger))
            Text("LIVE THIS $day", style = PulseType.Overline.copy(color = Ink, letterSpacing = 0.66.sp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(mock.title, style = PulseType.HeroTitle.copy(color = White))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetaWithIcon(R.drawable.ic_calendar, dateLabel)
                MetaWithIcon(R.drawable.ic_users, "${NumberFormat.getIntegerInstance(Locale.US).format(mock.joinedCount)} joined")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CountdownTile("%02d".format(countdown.days), "Days", Modifier.weight(1f))
            CountdownTile("%02d".format(countdown.hours), "Hours", Modifier.weight(1f))
            CountdownTile("%02d".format(countdown.minutes), "Mins", Modifier.weight(1f))
            CountdownTile("%02d".format(countdown.seconds), "Secs", Modifier.weight(1f))
        }
        PulseButton(
            label = if (mock.isReserved) "Seat reserved" else "Reserve my seat",
            onClick = onReserve,
            style = if (mock.isReserved) PulseButtonStyle.Soft else PulseButtonStyle.Lime,
            showIcon = !mock.isReserved,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MetaWithIcon(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(painterResource(icon), null, tint = TextTertiary, modifier = Modifier.size(16.dp))
        Text(text, style = PulseType.Meta.copy(color = TextTertiary))
    }
}

@Composable
private fun CountdownTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Ink2)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = PulseType.Question.copy(color = White, fontSize = 22.sp, lineHeight = 28.sp), textAlign = TextAlign.Center)
        Text(label, style = PulseType.Overline.copy(color = TextTertiary, letterSpacing = 0.sp, fontWeight = FontWeight.Medium))
    }
}

@Composable
fun MockList(mocks: List<FullLengthMock>, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        mocks.forEachIndexed { i, mock ->
            if (i > 0) HorizontalDivider(color = Line, thickness = 1.dp)
            MockRow(mock, onClick = { onClick(mock.id) })
        }
    }
}

@Composable
private fun MockRow(mock: FullLengthMock, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(SurfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            Text("%02d".format(mock.number), style = PulseType.ListTitle.copy(color = Ink, fontWeight = FontWeight.ExtraBold))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(mock.title, style = PulseType.ListTitle.copy(color = Ink))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val (bg, fg) = when (mock.difficulty) {
                    Difficulty.EASY -> SuccessSoft to Success
                    Difficulty.MEDIUM -> AmberSoft to Ink
                    Difficulty.HARD -> DangerSoft to Danger
                }
                Pill(mock.difficulty.label, bg, fg, horizontalPadding = 8.dp, verticalPadding = 3.dp, style = PulseType.Overline.copy(letterSpacing = 0.sp))
                Text("${mock.questionCount} Qs · ${mock.durationMinutes} min", style = PulseType.Caption.copy(color = TextSecondary))
            }
        }
        when (val status = mock.status) {
            is MockStatus.Completed -> Column(horizontalAlignment = Alignment.End) {
                Text("Scored", style = PulseType.Overline.copy(color = TextTertiary, letterSpacing = 0.sp, fontWeight = FontWeight.Medium))
                Text("${status.score}/${status.maxScore}", style = PulseType.ListTitle.copy(color = Ink, fontWeight = FontWeight.ExtraBold))
            }
            MockStatus.Available -> IconBadge(R.drawable.ic_play, size = 40.dp, iconSize = 16.dp, container = Ink, tint = Lime, contentDescription = "Start ${mock.title}")
            MockStatus.Locked -> IconBadge(R.drawable.ic_lock, size = 40.dp, iconSize = 16.dp, container = SurfaceMuted, tint = TextTertiary, contentDescription = "Locked")
        }
    }
}

/** Dashed "Build a custom mock" card. */
@Composable
fun CustomMockCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val radius = 24.dp
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .background(AppCanvas)
            .drawBehind {
                drawRoundRect(
                    color = TextTertiary,
                    cornerRadius = CornerRadius(radius.toPx()),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))),
                )
            }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 19.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(R.drawable.ic_sliders, size = 48.dp, iconSize = 22.dp, container = LimeSoft, tint = LimeDeep, shape = RoundedCornerShape(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Build a custom mock", style = PulseType.ListTitle.copy(color = Ink))
            Text("Pick subjects, length & difficulty", style = PulseType.Meta.copy(color = TextSecondary))
        }
        IconBadge(R.drawable.ic_arrow_right, size = 40.dp, iconSize = 18.dp, container = Ink, tint = Lime)
    }
}
