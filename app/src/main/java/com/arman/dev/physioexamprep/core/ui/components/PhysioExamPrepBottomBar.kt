package com.arman.dev.physioexamprep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.util.PhysioExamPrepTabs
import com.arman.dev.physioexamprep.core.util.providePhysioExamPrepTabs

/**
 * Figma component "BottomNav" (6:176): floating ink pill, 68dp tall, 34dp corners.
 * Active tab = lime capsule with 20dp icon + label; inactive = 48dp box with 22dp grey icon.
 */
@Composable
fun PhysioExamPrepBottomBar(
    modifier: Modifier = Modifier,
    tabs: List<PhysioExamPrepTabs> = providePhysioExamPrepTabs(),
    currentTabId: Int = 1,
    onTabClick: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(34.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(14.dp, shape, ambientColor = Ink.copy(alpha = 0.22f), spotColor = Ink.copy(alpha = 0.22f))
            .clip(shape)
            .background(Ink, shape)
            .height(68.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tabs.forEach { tab ->
            BottomBarIcon(isSelected = tab.id == currentTabId, tab = tab, onClick = onTabClick)
        }
    }
}

@Composable
fun BottomBarIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
    tab: PhysioExamPrepTabs = PhysioExamPrepTabs(),
    onClick: (Int) -> Unit,
) {
    if (isSelected) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Lime)
                .clickable(role = Role.Tab) { onClick(tab.id) }
                .padding(start = 14.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(tab.icon), contentDescription = null, tint = Ink, modifier = Modifier.size(20.dp))
            Text(tab.text, style = PulseType.BottomNav.copy(color = Ink))
        }
    } else {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(role = Role.Tab) { onClick(tab.id) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(tab.icon), contentDescription = tab.text, tint = TextTertiary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    PhysioExamPrepBottomBar(onTabClick = {})
}
