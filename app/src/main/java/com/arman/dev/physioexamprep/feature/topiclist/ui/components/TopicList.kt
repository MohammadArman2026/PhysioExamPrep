package com.arman.dev.physioexamprep.feature.topiclist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Line
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.util.Topic
import com.arman.dev.physioexamprep.core.util.provideTopics

@Composable
fun TopicList(
    modifier: Modifier = Modifier,
    topicList: List<Topic> = provideTopics()
){
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp , 24.dp, 0.dp,0.dp))
            .background(AppCanvas)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            items(topicList ,
                key = {
                    it.topicId
                }){topic->
                TopicItem(topic = topic)
            }
        }
    }
}

@Composable
fun TopicItem(modifier: Modifier = Modifier,
              topic: Topic){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardSurface).border(1.dp,Line,RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ){
            Text(
                text = topic.topicName,
                style = PulseType.BodyStrong.copy(Ink)
            )
            Text(
                text = "${topic.totalQuestion} QS",
                style = PulseType.Caption.copy(TextSecondary)
            )
        }
    }
}