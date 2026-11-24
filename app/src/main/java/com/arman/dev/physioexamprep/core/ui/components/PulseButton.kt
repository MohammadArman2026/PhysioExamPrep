package com.arman.dev.physioexamprep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.Ink as InkColor
import com.arman.dev.physioexamprep.core.ui.theme.Ink3 as Ink3Color
import com.arman.dev.physioexamprep.core.ui.theme.Lime as LimeColor
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.White as WhiteColor

/** Figma component "Button" (6:197): Style = Lime | Ink | Outline | Soft. */
enum class PulseButtonStyle(val container: Color, val content: Color, val border: Color?) {
    Lime(LimeColor, InkColor, null),
    Ink(InkColor, WhiteColor, null),
    Outline(WhiteColor, InkColor, InkColor),
    Soft(Ink3Color, WhiteColor, null),
}

/** 56dp, 18dp corners, bold 16sp label, optional trailing arrow (20dp). */
@Composable
fun PulseButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PulseButtonStyle = PulseButtonStyle.Lime,
    showIcon: Boolean = true,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(if (enabled) style.container else style.container.copy(alpha = 0.4f), shape)
            .then(if (style.border != null) Modifier.border(1.5.dp, style.border, shape) else Modifier)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = PulseType.Button.copy(color = style.content),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (showIcon) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = style.content,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PulseButtonPreview() {
    androidx.compose.foundation.layout.Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PulseButtonStyle.entries.forEach { PulseButton(label = "Continue", onClick = {}, style = it) }
    }
}
