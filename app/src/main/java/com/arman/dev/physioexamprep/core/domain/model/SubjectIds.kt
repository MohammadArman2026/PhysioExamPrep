package com.arman.dev.physioexamprep.core.domain.model

/**
 * Stable subject IDs shared by every feature (and the fake repositories).
 * 1–4 match the rows already in assets/physio_exam_prep.db; 5–6 are not in the DB yet.
 */
object SubjectIds {
    const val ANATOMY = 1L
    const val EXERCISE_THERAPY = 2L
    const val ELECTROTHERAPY = 3L
    const val ORTHOPEDICS = 4L
    const val PHYSIOLOGY = 5L
    const val NEUROLOGY = 6L
}
