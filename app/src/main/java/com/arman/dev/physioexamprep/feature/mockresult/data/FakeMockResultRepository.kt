package com.arman.dev.physioexamprep.feature.mockresult.data

import com.arman.dev.physioexamprep.core.domain.model.ScoredSession
import com.arman.dev.physioexamprep.core.domain.model.SessionResultStore
import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResult
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResultRepository
import com.arman.dev.physioexamprep.feature.mockresult.domain.Ranking
import com.arman.dev.physioexamprep.feature.mockresult.domain.SubjectBreakdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * "mock-1"/"mock-2" return the Figma "08 · Mock result" sample. Any other id is a session the
 * Quiz feature just finished (read from [SessionResultStore]).
 */
@Singleton
class FakeMockResultRepository @Inject constructor(
    private val store: SessionResultStore,
) : MockResultRepository {

    private val figmaSample = MockResult(
        id = "mock-2",
        title = "Mock #2",
        score = 164,
        maxScore = 200,
        ranking = Ranking(topPercent = 12, rank = 281, candidates = 2340),
        accuracyPercent = 82,
        timeTakenSeconds = 108 * 60L,
        attempted = 188,
        totalQuestions = 200,
        subjects = listOf(
            SubjectBreakdown(SubjectIds.PHYSIOLOGY, "Physiology", 41, 45, +14),
            SubjectBreakdown(SubjectIds.ANATOMY, "Anatomy", 44, 50, +9),
            SubjectBreakdown(SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 38, 50, +3),
            SubjectBreakdown(SubjectIds.ORTHOPEDICS, "Orthopedics", 21, 30, -2),
            SubjectBreakdown(SubjectIds.ELECTROTHERAPY, "Electrotherapy", 13, 25, -11),
        ),
    )

    override fun observeResult(resultId: String): Flow<MockResult?> = when (resultId) {
        "mock-2" -> flowOf(figmaSample)
        "mock-1" -> flowOf(figmaSample.copy(id = "mock-1", title = "Mock #1", score = 144, ranking = Ranking(21, 492, 2340), accuracyPercent = 74))
        else -> store.observe(resultId).map { it?.toMockResult() }
    }

    private fun ScoredSession.toMockResult() = MockResult(
        id = id,
        title = title,
        score = correct,
        maxScore = totalQuestions,
        ranking = null,
        accuracyPercent = if (attempted == 0) 0 else correct * 100 / attempted,
        timeTakenSeconds = timeTakenSeconds,
        attempted = attempted,
        totalQuestions = totalQuestions,
        subjects = subjects.map { SubjectBreakdown(it.subjectId, it.subjectName, it.correct, it.total, deltaVsAverage = null) }
            .sortedByDescending { it.percent },
    )
}
