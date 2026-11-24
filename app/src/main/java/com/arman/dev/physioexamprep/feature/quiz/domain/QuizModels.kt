package com.arman.dev.physioexamprep.feature.quiz.domain

import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest

data class QuizQuestion(
    val id: Long,
    val subjectId: Long,
    val subjectName: String,
    val topicId: Long,
    val topicName: String,
    val text: String,
    /** In display order: A, B, C, D. */
    val options: List<QuizOption>,
    val correctOptionId: Long,
    val explanation: String,
    /** First tag is highlighted in lime (Figma: "Rotator cuff"). */
    val tags: List<String>,
    /** "64% got it right" — share of all users answering correctly. */
    val communityCorrectPercent: Int,
)

data class QuizOption(val id: Long, val text: String)

data class QuizSession(
    val id: String,
    val title: String,
    val mode: PracticeMode,
    val questions: List<QuizQuestion>,
    /** null = untimed (the chip counts up instead of down). */
    val timeLimitSeconds: Int?,
)

interface QuizRepository {
    suspend fun startSession(request: QuizRequest): QuizSession

    /** Scores the answers (questionId → optionId), stores the result and returns its id. */
    suspend fun submit(session: QuizSession, answers: Map<Long, Long>, elapsedSeconds: Long): String

    suspend fun setBookmarked(questionId: Long, bookmarked: Boolean)
    suspend fun setFlagged(questionId: Long, flagged: Boolean)
}
