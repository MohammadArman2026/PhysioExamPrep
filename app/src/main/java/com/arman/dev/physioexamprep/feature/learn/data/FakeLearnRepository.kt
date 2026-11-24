package com.arman.dev.physioexamprep.feature.learn.data

import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.learn.domain.LearnOverview
import com.arman.dev.physioexamprep.feature.learn.domain.LearnRepository
import com.arman.dev.physioexamprep.feature.learn.domain.SubjectSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/** Data from the Figma "02 · Learn" frame. */
@Singleton
class FakeLearnRepository @Inject constructor() : LearnRepository {
    override fun observeOverview(): Flow<LearnOverview> = flowOf(
        LearnOverview(
            subjects = listOf(
                SubjectSummary(SubjectIds.ANATOMY, "Anatomy", 1240, 12, 45, isSaved = true),
                SubjectSummary(SubjectIds.PHYSIOLOGY, "Physiology", 980, 10, 62),
                SubjectSummary(SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 860, 8, 30),
                SubjectSummary(SubjectIds.ELECTROTHERAPY, "Electrotherapy", 640, 7, 12, isSaved = true),
                SubjectSummary(SubjectIds.ORTHOPEDICS, "Orthopedics", 920, 11, 8),
                SubjectSummary(SubjectIds.NEUROLOGY, "Neurology", 1600, 14, 0, isNew = true),
            ),
            overallMasteryPercent = 47,
            weeklyChangePercent = 6,
        )
    )
}
