package com.arman.dev.physioexamprep.feature.learn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.util.Subject
import com.arman.dev.physioexamprep.core.util.provideSubjects
import com.arman.dev.physioexamprep.feature.learn.ui.components.LearnScreenTopBar
import com.arman.dev.physioexamprep.feature.learn.ui.components.SubjectItem

@Composable
fun LearnScreen(modifier: Modifier = Modifier,
                subjectList: List<Subject> = provideSubjects(),
                onSubjectClick:(Int)-> Unit
){
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        LearnScreenTopBar()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(subjectList,
                key = { it.id}){subject->
                SubjectItem(
                    subject = subject,
                    onSubjectClick = onSubjectClick
                )
            }
        }
    }
}


