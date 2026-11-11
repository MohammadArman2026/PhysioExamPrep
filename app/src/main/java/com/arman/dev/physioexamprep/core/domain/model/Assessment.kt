package com.arman.dev.physioexamprep.core.domain.model

data class AssessmentSession(
    val id: String,
    val userId: String,
    val sessionType: SessionType,
    val title: String,
    val mockDefinitionId: String?,
    val sourcePaperId: Long?,
    val durationSeconds: Long?,
    val startedAt: Long,
    val completedAt: Long?,
    val currentQuestionIndex: Int,
    val status: SessionStatus
)

/** One question slot of a session and the user's answer state (no question content). */
data class SessionQuestionState(
    val sessionQuestionId: String,
    val questionId: Long,
    val position: Int,
    val marks: Double,
    val negativeMarks: Double,
    val answerStatus: AnswerStatus,
    val selectedOptionIds: Set<Long>,
    val isMarkedForReview: Boolean,
    val isVisited: Boolean,
    val timeSpentSeconds: Long,
    val answeredAt: Long?
)

data class AssessmentSessionDetail(
    val session: AssessmentSession,
    /** In position order. */
    val questions: List<SessionQuestionState>
)

/** Immediate feedback for practice-style sessions. */
data class AnswerOutcome(
    val isCorrect: Boolean,
    val correctOptionIds: Set<Long>
)

data class SessionResult(
    val sessionId: String,
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unattemptedQuestions: Int,
    val totalMarks: Double,
    val obtainedMarks: Double,
    /** 0–100. */
    val accuracy: Double,
    val timeTakenSeconds: Long,
    val completedAt: Long
)

data class CompletedAssessment(
    val session: AssessmentSession,
    val result: SessionResult
)
