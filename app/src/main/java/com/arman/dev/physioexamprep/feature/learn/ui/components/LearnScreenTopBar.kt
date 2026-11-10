package com.arman.dev.physioexamprep.feature.learn.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextPrimary
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary

@Composable
fun LearnScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "Learn",
    totalQuestion :Int = 600,
    totalSubject :Int = 6
){
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = PulseType.ScreenTitle.copy(Ink)
        )
        Text(
            text = "$totalSubject + subjects & $totalQuestion questions",
            style = PulseType.BodySmall.copy(color = TextSecondary)
        )
    }
}