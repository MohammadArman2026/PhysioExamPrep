package com.arman.dev.physioexamprep.core.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lime
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextTertiary
import com.arman.dev.physioexamprep.core.util.PhysioExamPrepTabs
import com.arman.dev.physioexamprep.core.util.providePhysioExamPrepTabs

@Composable
fun PhysioExamPrepBottomBar(modifier:Modifier = Modifier,
                            tabs:List<PhysioExamPrepTabs> = providePhysioExamPrepTabs(),
                            currentTabId :Int = 1,
                            onTabClick :(Int)-> Unit
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Ink, RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEach {tab ->
                BottomBarIcon(
                    modifier = Modifier,
                    isSelected = tab.id == currentTabId,
                    tab = tab,
                    onClick = onTabClick
                )
            }
        }
    }
}

@Composable
fun BottomBarIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
    tab: PhysioExamPrepTabs = PhysioExamPrepTabs(),
    onClick: (Int) -> Unit
){
    Box(
        modifier = modifier.then(
            if (isSelected) modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Lime,
                    RoundedCornerShape(24.dp)) else modifier
        ).clickable{
            onClick(tab.id)
        },
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(tab.icon),
                tint = if (isSelected)Ink else TextTertiary,
                contentDescription = "bottom_nav",
                modifier = Modifier.size(24.dp)
            )
            if(isSelected){
                Text(
                    text = tab.text ,
                    style = PulseType.BottomNav
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun Preview(){
    PhysioExamPrepBottomBar(
        onTabClick = {

        }
    )
}
