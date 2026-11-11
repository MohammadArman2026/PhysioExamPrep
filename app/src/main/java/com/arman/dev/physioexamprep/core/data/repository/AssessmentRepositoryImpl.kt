package com.arman.dev.physioexamprep.core.data.repository

import androidx.room.withTransaction
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.data.mapper.toDomain
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionResultEntity
import com.arman.dev.physioexamprep.core.domain.model.AnswerOutcome
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSession
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSessionDetail
import com.arman.dev.physioexamprep.core.domain.model.CompletedAssessment
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionResult
import com.arman.dev.physioexamprep.core.domain.model.SessionStatus
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import com.arman.dev.physioexamprep.core.domain.repository.AssessmentRepository
import com.arman.dev.physioexamprep.core.domain.scoring.AssessmentScorer
import com.arman.dev.physioexamprep.core.domain.scoring.ScoringItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class AssessmentRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : AssessmentRepository {

    private val sessionDao = database.assessmentSessionDao()
    private val sessionQuestionDao = database.sessionQuestionDao()
    private val answerDao = database.sessionAnswerDao()
    private val resultDao = database.sessionResultDao()
    private val outbox = SyncOutboxWriter(database.syncOutboxDao())
    private val progressRecorder = ProgressRecorder(database, timeProvider, outbox)

    // ---------------------------------------------------------------- start

    override suspend fun startSession(
        userId: String,
        sessionType: SessionType,
        title: String,
        questions: List<QuestionSelection>,
        durationSeconds: Long?,
        mockDefinitionId: String?,
        sourcePaperId: Long?
    ): String {
        require(questions.isNotEmpty()) { "A session needs at least one question" }
        require(questions.map { it.questionId }.toSet().size == questions.size) { "Duplicate questions in session" }
        return database.withTransaction {
            val now = timeProvider.nowMillis()
            val sessionId = UUID.randomUUID().toString()
            sessionDao.createSession(
                AssessmentSessionEntity(
                    id = sessionId,
                    userId = userId,
                    sessionType = sessionType,
                    title = title,
                    mockDefinitionId = mockDefinitionId,
                    sourcePaperId = sourcePaperId,
                    durationSeconds = durationSeconds,
                    startedAt = now,
                    completedAt = null,
                    status = SessionStatus.IN_PROGRESS,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null
                )
            )
            val sessionQuestions = questions.mapIndexed { index, q ->
                SessionQuestionEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    questionId = q.questionId,
                    position = index,
                    marks = q.marks,
                    negativeMarks = q.negativeMarks
                )
            }
            sessionQuestionDao.insertSessionQuestions(sessionQuestions)
            // One answer row per question up front, so later updates are plain UPDATEs.
            answerDao.insertAnswers(
                sessionQuestions.map {
                    SessionAnswerEntity(
                        id = UUID.randomUUID().toString(),
                        sessionQuestionId = it.id,
                        answerStatus = AnswerStatus.UNANSWERED,
                        answeredAt = null,
                        updatedAt = now
                    )
                }
            )
            outbox.enqueue(userId, SyncEntityType.ASSESSMENT_SESSION, sessionId, SyncOperation.INSERT, now)
            sessionId
        }
    }

    // ---------------------------------------------------------------- observe

    override fun observeSession(sessionId: String): Flow<AssessmentSessionDetail?> =
        sessionDao.observeSessionWithQuestions(sessionId).map { it?.toDomain() }

    override fun observeActiveSessions(userId: String): Flow<List<AssessmentSession>> =
        sessionDao.observeActiveSessions(userId).map { list -> list.map { it.toDomain() } }

    override fun observeSessions(userId: String): Flow<List<AssessmentSession>> =
        sessionDao.observeSessions(userId).map { list -> list.map { it.toDomain() } }

    override fun observeResult(sessionId: String): Flow<SessionResult?> =
        resultDao.observeResult(sessionId).map { it?.toDomain() }

    override fun observeCompletedAssessments(userId: String): Flow<List<CompletedAssessment>> =
        sessionDao.observeCompletedSessionsWithResults(userId).map { list ->
            list.mapNotNull { item -> item.result?.let { CompletedAssessment(item.session.toDomain(), it.toDomain()) } }
        }

    // ---------------------------------------------------------------- answers

    override suspend fun saveAnswer(
        sessionQuestionId: String,
        selectedOptionIds: Set<Long>,
        additionalTimeSeconds: Long
    ): AnswerOutcome? = database.withTransaction {
        val now = timeProvider.nowMillis()
        val (sessionQuestion, session) = requireInProgress(sessionQuestionId)
        val answer = checkNotNull(answerDao.getAnswerEntity(sessionQuestionId)) {
            "No answer row for session question $sessionQuestionId"
        }
        val immediate = session.sessionType.evaluatesImmediately
        check(!(immediate && answer.answerStatus == AnswerStatus.ANSWERED)) {
            "This question was already checked in a practice session"
        }

        val validOptionIds = sessionQuestionDao.getOptionIdsForQuestion(sessionQuestion.questionId).toSet()
        require(validOptionIds.containsAll(selectedOptionIds)) {
            "Selected options do not belong to question ${sessionQuestion.questionId}"
        }
        val evaluationRow = checkNotNull(sessionQuestionDao.getEvaluationRow(sessionQuestionId))
        if (evaluationRow.questionType == QuestionEntity.QuestionType.SINGLE_CORRECT) {
            require(selectedOptionIds.size <= 1) { "Single-correct question allows only one option" }
        }

        answerDao.saveAnswer(answer.id, selectedOptionIds, additionalTimeSeconds, now)

        if (!immediate || selectedOptionIds.isEmpty()) return@withTransaction null

        // Practice: evaluate now, record progress and daily activity in the same transaction.
        val correctOptionIds = sessionQuestionDao.getCorrectOptionIds(sessionQuestion.questionId).toSet()
        val isCorrect = AssessmentScorer.isCorrect(selectedOptionIds, correctOptionIds)
        progressRecorder.recordQuestionOutcome(
            userId = session.userId,
            questionId = sessionQuestion.questionId,
            chapterId = evaluationRow.chapterId,
            isCorrect = isCorrect,
            at = now
        )
        progressRecorder.recordDailyActivity(
            userId = session.userId,
            questionsAttempted = 1,
            timeSpentSeconds = answer.timeSpentSeconds + additionalTimeSeconds,
            at = now
        )
        AnswerOutcome(isCorrect = isCorrect, correctOptionIds = correctOptionIds)
    }

    override suspend fun setMarkedForReview(sessionQuestionId: String, isMarkedForReview: Boolean) {
        database.withTransaction<Unit> {
            requireInProgress(sessionQuestionId)
            answerDao.updateReviewStatus(sessionQuestionId, isMarkedForReview, timeProvider.nowMillis())
        }
    }

    override suspend fun markVisited(sessionQuestionId: String, additionalTimeSeconds: Long) {
        require(additionalTimeSeconds >= 0) { "Time spent cannot be negative" }
        database.withTransaction<Unit> {
            requireInProgress(sessionQuestionId)
            val now = timeProvider.nowMillis()
            answerDao.updateVisitedStatus(sessionQuestionId, true, now)
            if (additionalTimeSeconds > 0) answerDao.addTimeSpent(sessionQuestionId, additionalTimeSeconds, now)
        }
    }

    override suspend fun updateCurrentQuestionIndex(sessionId: String, index: Int) {
        require(index >= 0) { "Index cannot be negative" }
        val updated = sessionDao.updateCurrentQuestionIndex(sessionId, index, timeProvider.nowMillis())
        check(updated == 1) { "Session $sessionId is not in progress" }
    }

    override suspend fun abandonSession(sessionId: String) {
        database.withTransaction<Unit> {
            val session = checkNotNull(sessionDao.getSessionById(sessionId)) { "Session $sessionId not found" }
            if (session.status != SessionStatus.IN_PROGRESS) return@withTransaction
            val now = timeProvider.nowMillis()
            sessionDao.updateSessionStatus(sessionId, SessionStatus.ABANDONED, now)
            outbox.enqueue(session.userId, SyncEntityType.ASSESSMENT_SESSION, sessionId, SyncOperation.UPDATE, now)
        }
    }

    // ---------------------------------------------------------------- complete

    override suspend fun completeSession(sessionId: String): SessionResult = database.withTransaction {
        val session = checkNotNull(sessionDao.getSessionById(sessionId)) { "Session $sessionId not found" }
        if (session.status == SessionStatus.COMPLETED) {
            return@withTransaction checkNotNull(resultDao.getResultBySessionId(sessionId)).toDomain()
        }
        check(session.status == SessionStatus.IN_PROGRESS) { "Session $sessionId was abandoned" }

        val now = timeProvider.nowMillis()
        val rows = sessionQuestionDao.getEvaluationRows(sessionId)
        val answers = answerDao.getAnswersForSession(sessionId).associateBy { it.answer.sessionQuestionId }
        val correctBySessionQuestion = sessionQuestionDao.getCorrectOptionsForSession(sessionId)
            .groupBy({ it.sessionQuestionId }, { it.optionId })

        val summary = AssessmentScorer.score(
            rows.map { row ->
                ScoringItem(
                    sessionQuestionId = row.sessionQuestionId,
                    marks = row.marks,
                    negativeMarks = row.negativeMarks,
                    selectedOptionIds = answers[row.sessionQuestionId]?.selectedOptionIds.orEmpty(),
                    correctOptionIds = correctBySessionQuestion[row.sessionQuestionId].orEmpty().toSet()
                )
            }
        )

        // Practice sessions already recorded progress per answer; mocks record it now.
        if (!session.sessionType.evaluatesImmediately) {
            rows.forEach { row ->
                val isCorrect = summary.outcomes[row.sessionQuestionId] ?: return@forEach
                progressRecorder.recordQuestionOutcome(session.userId, row.questionId, row.chapterId, isCorrect, now)
            }
            progressRecorder.recordDailyActivity(
                userId = session.userId,
                questionsAttempted = summary.attemptedQuestions,
                timeSpentSeconds = answers.values.sumOf { it.answer.timeSpentSeconds },
                at = now
            )
        }

        val elapsedSeconds = ((now - session.startedAt) / 1000).coerceAtLeast(0)
        val timeTaken = session.durationSeconds?.let { elapsedSeconds.coerceAtMost(it) } ?: elapsedSeconds

        check(sessionDao.completeSession(sessionId, now) == 1) { "Session $sessionId could not be completed" }
        val result = SessionResultEntity(
            sessionId = sessionId,
            totalQuestions = summary.totalQuestions,
            attemptedQuestions = summary.attemptedQuestions,
            correctAnswers = summary.correctAnswers,
            incorrectAnswers = summary.incorrectAnswers,
            unattemptedQuestions = summary.unattemptedQuestions,
            totalMarks = summary.totalMarks,
            obtainedMarks = summary.obtainedMarks,
            accuracy = summary.accuracy,
            timeTakenSeconds = timeTaken,
            completedAt = now
        )
        resultDao.insertResult(result)
        outbox.enqueue(session.userId, SyncEntityType.ASSESSMENT_SESSION, sessionId, SyncOperation.UPDATE, now)
        outbox.enqueue(session.userId, SyncEntityType.SESSION_RESULT, sessionId, SyncOperation.INSERT, now)
        result.toDomain()
    }

    // ---------------------------------------------------------------- helpers

    private suspend fun requireInProgress(sessionQuestionId: String): Pair<SessionQuestionEntity, AssessmentSessionEntity> {
        val sessionQuestion = checkNotNull(sessionQuestionDao.getSessionQuestionById(sessionQuestionId)) {
            "Session question $sessionQuestionId not found"
        }
        val session = checkNotNull(sessionDao.getSessionById(sessionQuestion.sessionId))
        check(session.status == SessionStatus.IN_PROGRESS) { "Session ${session.id} is not in progress" }
        return sessionQuestion to session
    }
}
