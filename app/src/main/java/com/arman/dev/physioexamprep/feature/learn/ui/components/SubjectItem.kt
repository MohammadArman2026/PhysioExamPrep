package com.arman.dev.physioexamprep.feature.learn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.util.Subject

@Composable
fun SubjectItem(modifier: Modifier = Modifier,
                subject: Subject,
                onSubjectClick:(Int)-> Unit){
    Box(modifier = modifier
        .height(140.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(subject.bg, RoundedCornerShape(24.dp))
        .clickable{
            onSubjectClick(subject.id)
        }) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.size(44.dp)
                    .clip(CircleShape)
                    .background(subject.iconBg,CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(subject.icon),
                    tint = subject.iconColor,
                    contentDescription = "subject item",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = subject.name,
                style = PulseType.BodyStrong
            )


            Text(
                text = "${subject.totalQuestion} Qs & ${subject.totalTopics} topics",
                style = PulseType.Caption
            )
        }
    }
}