package com.arman.dev.physioexamprep.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.Pill
import com.arman.dev.physioexamprep.core.ui.components.pulseCard
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.DangerSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
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
import com.arman.dev.physioexamprep.feature.profile.domain.ProfileOverview
import com.arman.dev.physioexamprep.feature.profile.presentation.ProfileIntent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IdentityCard(profile: ProfileOverview, initials: String, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(Modifier.size(72.dp).clip(RoundedCornerShape(24.dp)).background(Lilac), contentAlignment = Alignment.Center) {
            Text(initials, style = PulseType.Question.copy(color = CardSurface))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(profile.name, style = PulseType.SheetTitle.copy(color = Ink))
            Text(profile.subtitle, style = PulseType.Meta.copy(color = TextSecondary))
            Pill("Target: ${profile.targetExam}", LimeSoft, LimeDeep, Modifier.padding(top = 4.dp))
        }
        IconBadge(R.drawable.ic_edit, size = 40.dp, iconSize = 18.dp, container = SurfaceMuted, contentDescription = "Edit profile", onClick = onEdit)
    }
}

@Composable
fun ProfileStats(profile: ProfileOverview, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatTile(R.drawable.ic_check, Success, SuccessSoft, NumberFormat.getIntegerInstance(Locale.US).format(profile.questionsSolved), "Solved", Modifier.weight(1f))
        StatTile(R.drawable.ic_award, Lilac, LilacSoft, ordinal(profile.percentile), "Percentile", Modifier.weight(1f))
        StatTile(R.drawable.ic_flame, Coral, CoralSoft, "${profile.streakDays} days", "Streak", Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(icon: Int, accent: Color, soft: Color, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .pulseCard(radius = 20.dp)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        IconBadge(icon, size = 32.dp, iconSize = 16.dp, container = soft, tint = accent, shape = RoundedCornerShape(10.dp))
        Column {
            Text(value, style = PulseType.SectionTitle.copy(color = Ink), maxLines = 1)
            Text(label, style = PulseType.Caption.copy(color = TextSecondary))
        }
    }
}

private fun ordinal(n: Int): String {
    val suffix = if (n % 100 in 11..13) "th" else when (n % 10) { 1 -> "st"; 2 -> "nd"; 3 -> "rd"; else -> "th" }
    return "$n$suffix"
}

private val heatLevels = listOf(SurfaceMuted, LimeSoft, Lime, LimeDeep)

/** 12-week activity heatmap (columns = weeks, rows = days). */
@Composable
fun ActivityCard(weeks: List<List<Int>>, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Activity", style = PulseType.CardTitle.copy(color = Ink), modifier = Modifier.weight(1f))
            Text("Last ${weeks.size} weeks", style = PulseType.Meta.copy(color = TextTertiary))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            weeks.forEach { days ->
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    days.forEach { level ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(heatLevels[level.coerceIn(0, heatLevels.lastIndex)])
                        )
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Less", style = PulseType.Caption.copy(color = TextTertiary))
            heatLevels.forEach { Box(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(it)) }
            Text("More", style = PulseType.Caption.copy(color = TextTertiary))
        }
    }
}

@Composable
fun SettingsCard(profile: ProfileOverview, onIntent: (ProfileIntent) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .pulseCard()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        SettingRow(R.drawable.ic_bell, "Study reminders", value = profile.reminderTime, onClick = { onIntent(ProfileIntent.RemindersClicked) })
        SettingRow(
            R.drawable.ic_moon, "Dark mode",
            onClick = { onIntent(ProfileIntent.DarkModeToggled(!profile.darkMode)) },
            trailing = { PulseToggle(profile.darkMode) },
        )
        SettingRow(R.drawable.ic_target, "Exam target", value = profile.targetExam, onClick = { onIntent(ProfileIntent.ExamTargetClicked) })
        SettingRow(R.drawable.ic_help, "Help & support", onClick = { onIntent(ProfileIntent.HelpClicked) })
        SettingRow(R.drawable.ic_logout, "Log out", danger = true, showChevron = false, onClick = { onIntent(ProfileIntent.LogoutClicked) })
    }
}

@Composable
private fun SettingRow(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    value: String? = null,
    danger: Boolean = false,
    showChevron: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(67.dp)
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(
            icon, size = 40.dp, iconSize = 18.dp,
            container = if (danger) DangerSoft else SurfaceMuted,
            tint = if (danger) Danger else Ink,
            shape = RoundedCornerShape(12.dp),
        )
        Text(title, style = PulseType.BodyStrong.copy(color = if (danger) Danger else Ink), modifier = Modifier.weight(1f))
        if (value != null) Text(value, style = PulseType.Chip.copy(color = TextSecondary, fontWeight = FontWeight.Medium))
        when {
            trailing != null -> trailing()
            showChevron -> Icon(painterResource(R.drawable.ic_chev_right), null, tint = TextTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

/** 46×28 toggle: off = muted track + white knob, on = ink track + lime knob (on-state is a guess). */
@Composable
private fun PulseToggle(checked: Boolean) {
    Box(
        Modifier
            .width(46.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(if (checked) Ink else SurfaceMuted)
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = if (checked) 18.dp else 0.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(if (checked) Lime else CardSurface)
        )
    }
}
