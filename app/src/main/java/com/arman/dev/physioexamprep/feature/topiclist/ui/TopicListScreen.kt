package com.arman.dev.physioexamprep.feature.topiclist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.feature.topiclist.ui.components.SubjectListHeader
import com.arman.dev.physioexamprep.feature.topiclist.ui.components.SubjectListTopBar
import com.arman.dev.physioexamprep.feature.topiclist.ui.components.TopicList

@Composable
fun TopicListScreen(
    modifier: Modifier = Modifier,
    onBackClick:()-> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CoralSoft)
    ) {
        SubjectListTopBar(
            onBackClick = onBackClick
        )
        Spacer(Modifier.height(16.dp))
        SubjectListHeader()
        Spacer(Modifier.height(16.dp))
        TopicList(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview(showBackground = true)
fun TopicListScreenPreview(){
    TopicListScreen(
        onBackClick = {}
    )
}