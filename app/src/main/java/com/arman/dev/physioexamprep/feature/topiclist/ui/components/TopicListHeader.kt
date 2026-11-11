package com.arman.dev.physioexamprep.feature.topiclist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary

@Composable
fun SubjectListHeader(
    modifier: Modifier = Modifier,
    subjectName : String = "Anatomy",
    totalTopics :Int = 12,
    totalQuestion :Int = 300
){
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Subject",
            style = PulseType.Overline.copy(color = Coral)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subjectName,
            style = PulseType.SubjectTitle.copy(Ink)
        )
        Text(
            text = "$totalTopics topics & $totalQuestion questions",
            style = PulseType.Overline.copy(color = TextSecondary)
        )
    }
}