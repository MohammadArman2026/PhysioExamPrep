package com.arman.dev.physioexamprep.core.domain.repository

import com.arman.dev.physioexamprep.core.domain.model.AnswerOutcome
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSession
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSessionDetail
import com.arman.dev.physioexamprep.core.domain.model.CompletedAssessment
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionResult
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import kotlinx.coroutines.flow.Flow

interface AssessmentRepository {

    /** Creates the session, its fixed question list and empty answer rows atomically. */
    suspend fun startSession(
        userId: String,
        sessionType: SessionType,
        title: String,
        questions: List<QuestionSelection>,
        durationSeconds: Long?,
        mockDefinitionId: String? = null,
        sourcePaperId: Long? = null
    ): String

    fun observeSession(sessionId: String): Flow<AssessmentSessionDetail?>
    fun observeActiveSessions(userId: String): Flow<List<AssessmentSession>>
    fun observeSessions(userId: String): Flow<List<AssessmentSession>>

    /**
     * Replaces the selected options atomically (empty = clear the answer).
     * For practice-style sessions the answer is evaluated immediately, locked,
     * progress/daily activity are updated in the same transaction, and the outcome
     * is returned. For mocks it returns null (evaluated on completion).
     */
    suspend fun saveAnswer(
        sessionQuestionId: String,
        selectedOptionIds: Set<Long>,
        additionalTimeSeconds: Long = 0
    ): AnswerOutcome?

    suspend fun setMarkedForReview(sessionQuestionId: String, isMarkedForReview: Boolean)
    suspend fun markVisited(sessionQuestionId: String, additionalTimeSeconds: Long = 0)
    suspend fun updateCurrentQuestionIndex(sessionId: String, index: Int)
    suspend fun abandonSession(sessionId: String)

    /**
     * Scores the session, stores the result snapshot and marks it COMPLETED in one
     * transaction (mocks also update progress here). Calling it again returns the
     * stored result.
     */
    suspend fun completeSession(sessionId: String): SessionResult

    fun observeResult(sessionId: String): Flow<SessionResult?>
    fun observeCompletedAssessments(userId: String): Flow<List<CompletedAssessment>>
}
