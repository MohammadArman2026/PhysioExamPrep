package com.arman.dev.physioexamprep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.DangerSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.Success
import com.arman.dev.physioexamprep.core.ui.theme.SuccessSoft
import com.arman.dev.physioexamprep.core.ui.theme.SurfaceMuted
import com.arman.dev.physioexamprep.core.ui.theme.White

/** Figma component "OptionRow" (6:219). */
enum class OptionState { Default, Selected, Correct, Wrong }

@Composable
fun OptionRow(
    letter: String,
    answer: String,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    data class Look(val bg: Color, val border: Color, val borderWidth: Dp, val badgeBg: Color, val badgeText: Color)
    val look = when (state) {
        OptionState.Default -> Look(CardSurface, Line, 1.5.dp, SurfaceMuted, Ink)
        OptionState.Selected -> Look(CardSurface, Ink, 2.dp, Ink, White)
        OptionState.Correct -> Look(SuccessSoft, Success, 2.dp, Success, White)
        OptionState.Wrong -> Look(DangerSoft, Danger, 2.dp, Danger, White)
    }
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(look.bg, shape)
            .border(look.borderWidth, look.border, shape)
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(start = 14.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(look.badgeBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(letter, style = PulseType.BodyStrong.copy(color = look.badgeText, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold))
        }
        Text(answer, style = PulseType.OptionText.copy(color = Ink), modifier = Modifier.weight(1f))
        when (state) {
            OptionState.Correct -> Icon(painterResource(R.drawable.ic_check), "Correct", tint = Success, modifier = Modifier.size(22.dp))
            OptionState.Wrong -> Icon(painterResource(R.drawable.ic_x), "Wrong", tint = Danger, modifier = Modifier.size(22.dp))
            else -> Unit
        }
    }
}
