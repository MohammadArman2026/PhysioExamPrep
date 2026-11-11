package com.arman.dev.physioexamprep.core.domain.model

data class QuestionProgress(
    val userId: String,
    val questionId: Long,
    val attemptCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val lastAttemptAt: Long?
)

data class ChapterProgress(
    val userId: String,
    val chapterId: Long,
    /** Distinct questions attempted at least once. */
    val attemptedQuestions: Int,
    /** Distinct questions answered correctly at least once. */
    val correctQuestions: Int,
    val lastPracticedAt: Long?
)

data class DailyActivity(
    val userId: String,
    /** YYYY-MM-DD */
    val activityDate: String,
    val questionsAttempted: Int,
    val timeSpentSeconds: Long
)
