package com.arman.dev.physioexamprep.feature.mockresult.domain

import kotlinx.coroutines.flow.Flow

data class MockResult(
    val id: String,
    val title: String,
    val score: Int,
    val maxScore: Int,
    /** Null for practice sessions (no leaderboard) — the rank pill is hidden. */
    val ranking: Ranking?,
    /** 0..100 */
    val accuracyPercent: Int,
    val timeTakenSeconds: Long,
    val attempted: Int,
    val totalQuestions: Int,
    val subjects: List<SubjectBreakdown>,
) {
    val strongest: SubjectBreakdown? get() = subjects.maxByOrNull { it.percent }
    val weakest: SubjectBreakdown? get() = subjects.minByOrNull { it.percent }?.takeIf { subjects.size > 1 }
}

data class Ranking(val topPercent: Int, val rank: Int, val candidates: Int)

data class SubjectBreakdown(
    val subjectId: Long,
    val name: String,
    val correct: Int,
    val total: Int,
    /** Percentage points vs. the average candidate; null when there's no cohort. */
    val deltaVsAverage: Int?,
) {
    val percent: Int get() = if (total == 0) 0 else correct * 100 / total
}

interface MockResultRepository {
    fun observeResult(resultId: String): Flow<MockResult?>
}
