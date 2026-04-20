package com.arman.dev.physioexamprep.feature.module.data

import com.arman.dev.physioexamprep.R

object Subjects {

    val all = listOf(
        SubjectUi(
            id = 1,
            subjectName = "Exercise Therapy",
            subjectDescription = "Practice MCQs on therapeutic exercises, gait training, posture, and rehabilitation.",
            icon = R.drawable.exercisetherapy
        ),

        SubjectUi(
            id = 2,
            subjectName = "Electro Therapy",
            subjectDescription = "Practice MCQs on TENS, IFT, ultrasound, currents, and electrotherapy modalities.",
            icon = R.drawable.electrotherapy
        ),

        SubjectUi(
            id = 3,
            subjectName = "Orthopedics",
            subjectDescription = "Practice MCQs on fractures, deformities, orthopedic conditions, and management.",
            icon = R.drawable.orthopedics
        ),

        SubjectUi(
            id = 4,
            subjectName = "Neuroscience",
            subjectDescription = "Practice MCQs on cranial nerves, reflexes, stroke, and neurological disorders.",
            icon = R.drawable.neuroscience
        ),

        SubjectUi(
            id = 5,
            subjectName = "Cardiopulmonary",
            subjectDescription = "Practice MCQs on respiratory care, cardiac rehab, ICU, and breathing exercises.",
            icon = R.drawable.cardiopulmonary
        ),

        SubjectUi(
            id = 6,
            subjectName = "Biomechanics",
            subjectDescription = "Practice MCQs on body mechanics, gait, posture, movement, and muscle actions.",
            icon = R.drawable.biomechanics
        ),

        SubjectUi(
            id = 7,
            subjectName = "Rehabilitation",
            subjectDescription = "Practice MCQs on disability management, assistive devices, and rehab programs.",
            icon = R.drawable.rehab
        ),

        SubjectUi(
            id = 8,
            subjectName = "Physiology & Anatomy",
            subjectDescription = "Practice MCQs on muscles, bones, nerves, organs, and body systems.",
            icon = R.drawable.physiologyanatomy
        ),

        SubjectUi(
            id = 9,
            subjectName = "Research & Biostat",
            subjectDescription = "Practice MCQs on research methods, statistics, study design, and data analysis.",
            icon = R.drawable.research_biostat
        )
    )
}