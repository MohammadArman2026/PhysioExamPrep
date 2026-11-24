package com.arman.dev.physioexamprep.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.TextPrimary
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.White

/** White card with the Figma "card" shadow (0 4 18 rgba(14,18,32,.05)). */
fun Modifier.pulseCard(radius: Dp = 24.dp, color: Color = CardSurface): Modifier {
    val shape = RoundedCornerShape(radius)
    return this
        .shadow(elevation = 6.dp, shape = shape, ambientColor = Ink.copy(alpha = 0.05f), spotColor = Ink.copy(alpha = 0.08f))
        .clip(shape)
        .background(color, shape)
}

/** Rounded box with a centred, tinted icon (Figma "Icon/xxx" frames). */
@Composable
fun IconBadge(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    container: Color = CardSurface,
    tint: Color = Ink,
    shape: Shape = CircleShape,
    borderColor: Color? = null,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(container, shape)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

/** Round 44–48dp outlined icon button used in screen headers (search, sliders, calendar, close). */
@Composable
fun HeaderIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 20.dp,
    bordered: Boolean = true,
) = IconBadge(
    icon = icon,
    modifier = modifier,
    size = size,
    iconSize = iconSize,
    container = CardSurface,
    tint = Ink,
    borderColor = if (bordered) Line else null,
    contentDescription = contentDescription,
    onClick = onClick,
)

/** Figma component "Chip" (6:224): filter chip, Default (white + line border) / Active (ink). */
@Composable
fun PulseChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = CircleShape
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) Ink else CardSurface, shape)
            .then(if (selected) Modifier else Modifier.border(1.dp, Line, shape))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(label, style = PulseType.Chip.copy(color = if (selected) White else TextSecondary))
    }
}

/** Small rounded label ("Strong", "Easy", "64% got it right"). */
@Composable
fun Pill(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 10.dp,
    verticalPadding: Dp = 5.dp,
    style: TextStyle = PulseType.Pill,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(container, CircleShape)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
    ) {
        Text(text, style = style.copy(color = content))
    }
}

/** Rounded progress bar: [fraction] 0..1 of [color] over a [track]. */
@Composable
fun ProgressTrack(
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    track: Color = SurfaceMuted,
    height: Dp = 6.dp,
) {
    val shape = RoundedCornerShape(height / 2)
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(track, shape),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .clip(shape)
                .background(color, shape),
        )
    }
}

/** Circular goal ring (Home hero): ink track, lime arc from 12 o'clock, % in the middle. */
@Composable
fun ProgressRing(
    fraction: Float,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    strokeWidth: Dp = 10.dp,
    track: Color = com.arman.dev.physioexamprep.core.ui.theme.Ink3,
    progress: Color = com.arman.dev.physioexamprep.core.ui.theme.Lime,
    label: String,
    labelColor: Color = White,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            val topLeft = Offset(sw / 2, sw / 2)
            drawArc(track, 0f, 360f, false, topLeft, arcSize, style = Stroke(sw))
            drawArc(progress, -90f, 360f * fraction.coerceIn(0f, 1f), false, topLeft, arcSize, style = Stroke(sw, cap = StrokeCap.Round))
        }
        Text(label, style = PulseType.SectionTitle.copy(color = labelColor, letterSpacing = 0.sp))
    }
}

/** Segmented control (PYQs "By exam/topic/year", Topic picker mode toggle). */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceMuted)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val shape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (selected) Modifier.shadow(3.dp, shape, ambientColor = Ink.copy(alpha = 0.08f), spotColor = Ink.copy(alpha = 0.08f))
                        else Modifier
                    )
                    .clip(shape)
                    .background(if (selected) CardSurface else Color.Transparent, shape)
                    .clickable(role = Role.Tab) { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option,
                    style = if (selected) PulseType.Chip.copy(color = TextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    else PulseType.Chip.copy(color = TextSecondary),
                )
            }
        }
    }
}

/** "Focus areas ........ See all" row. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = PulseType.SectionTitle.copy(color = Ink), modifier = Modifier.weight(1f))
        if (action != null) {
            Text(
                action,
                style = PulseType.Chip.copy(color = TextSecondary),
                modifier = if (onAction != null) Modifier.clickable(role = Role.Button, onClick = onAction) else Modifier,
            )
        }
    }
}

/** Tab screen header: 30sp title, 14sp subtitle, optional round action on the right. */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = PulseType.ScreenTitle.copy(color = Ink))
            Text(subtitle, style = PulseType.BodySmall.copy(color = TextSecondary))
        }
        trailing?.invoke()
    }
}
