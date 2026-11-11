package com.arman.dev.physioexamprep.core.data.repository

import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.database.AppDatabase

/**
 * Writes question/chapter progress and daily activity. Only called from inside
 * AssessmentRepositoryImpl transactions, so progress always matches the attempts.
 */
internal class ProgressRecorder(
    database: AppDatabase,
    private val timeProvider: TimeProvider
) {
    private val questionProgressDao = database.questionProgressDao()
    private val chapterProgressDao = database.chapterProgressDao()
    private val dailyActivityDao = database.dailyActivityDao()

    /** Updates question progress, then chapter progress (distinct-question counters). */
    suspend fun recordQuestionOutcome(
        userId: String,
        questionId: Long,
        chapterId: Long,
        isCorrect: Boolean,
        at: Long
    ) {
        val update = questionProgressDao.updateQuestionProgress(userId, questionId, isCorrect, at)

        chapterProgressDao.updateChapterProgress(
            userId = userId,
            chapterId = chapterId,
            attemptedIncrement = if (update.isFirstAttempt) 1 else 0,
            correctIncrement = if (update.isFirstCorrect) 1 else 0,
            practicedAt = at
        )
    }

    suspend fun recordDailyActivity(userId: String, questionsAttempted: Int, timeSpentSeconds: Long, at: Long) {
        if (questionsAttempted == 0 && timeSpentSeconds == 0L) return
        dailyActivityDao.updateDailyActivity(
            userId = userId,
            activityDate = timeProvider.localDate(at),
            questionsIncrement = questionsAttempted,
            timeIncrementSeconds = timeSpentSeconds,
            updatedAt = at
        )
    }
}
