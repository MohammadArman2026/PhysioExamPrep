package com.arman.dev.physioexamprep.core.util

import com.arman.dev.physioexamprep.R


data class PhysioExamPrepTabs(
    val id:Int = 1, //for home
    val text : String = "Home",
    val icon: Int = R.drawable.ic_home
)

fun providePhysioExamPrepTabs() = listOf<PhysioExamPrepTabs>(
    PhysioExamPrepTabs(),
    PhysioExamPrepTabs(
        id = 2,
        text = "Learn",
        icon = R.drawable.ic_book
    ),
    PhysioExamPrepTabs(
        id = 3,
        text = "PYQs",
        icon = R.drawable.ic_file
    ),
    PhysioExamPrepTabs(
        id = 4,
        text = "Mocks",
        icon = R.drawable.ic_timer
    ),
    PhysioExamPrepTabs(
        id = 5,
        text = "Me",
        icon = R.drawable.ic_user
    )
)
