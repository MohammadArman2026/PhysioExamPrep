package com.arman.dev.physioexamprep.feature.module.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.cardColor
import com.arman.dev.physioexamprep.core.ui.theme.iconColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleTopBar(title: String,
                 navigationIcon: Int,
                 modifier: Modifier = Modifier,
                 topBarColor: Color = cardColor,
                 iconTint: Color = iconColor,
                 titleColor: Color = MaterialTheme.colorScheme.primary,
                 scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
){
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor
                    )
                )
            }
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = navigationIcon),
                contentDescription = "App Logo",
                tint = iconTint,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(28.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topBarColor
        ),
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ModuleTopBarPreview(){
    ModuleTopBar(
        title = "Physio Exam Prep" ,
        navigationIcon = R.drawable.physio_exam_icon ,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}
