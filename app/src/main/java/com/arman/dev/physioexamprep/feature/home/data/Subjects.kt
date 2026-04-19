package com.arman.dev.physioexamprep.feature.home.data

import com.arman.dev.physioexamprep.R

object Subjects {
    val all = listOf(
        SubjectUi(id = 1, subjectName = "Exercise Therapy", R.drawable.exercisetherapy),
        SubjectUi(id = 2, subjectName = "Electro Therapy", R.drawable.electrotherapy),
        SubjectUi(id = 3, subjectName = "Orthopedics" , R.drawable.orthopedics),
        SubjectUi(id = 4 , subjectName = "Neuroscience" , R.drawable.neuroscience) ,
        SubjectUi(id = 5, subjectName = "Cardiopulmonary" , R.drawable.cardiopulmonary),
        SubjectUi(id = 6, subjectName = "Biomechanics" ,R.drawable.biomechanics),
        SubjectUi(id = 7, subjectName = "Rehabilitation", R.drawable.rehab),
        SubjectUi(id = 8, subjectName = "Physiology & Anatomy",R.drawable.physiologyanatomy),
        SubjectUi(id = 9, subjectName =  "Research & Biostat" , R.drawable.research_biostat)
    )
}