package com.arman.dev.physioexamprep.core.domain.model

import kotlinx.coroutines.flow.Flow

/**
 * A finished quiz/mock, handed from the Quiz feature to the Mock result feature.
 * Kept in core so neither feature depends on the other.
 */
data class ScoredSession(
    val id: String,
    val title: String,
    val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val timeTakenSeconds: Long,
    val subjects: List<SubjectScore>,
)

data class SubjectScore(val subjectId: Long, val subjectName: String, val correct: Int, val total: Int)

interface SessionResultStore {
    suspend fun save(session: ScoredSession)
    fun observe(id: String): Flow<ScoredSession?>
}
