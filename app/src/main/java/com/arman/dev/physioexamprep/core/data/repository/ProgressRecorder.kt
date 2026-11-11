package com.arman.dev.physioexamprep.core.data.repository

import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation

/**
 * Writes question/chapter progress and daily activity. Only called from inside
 * AssessmentRepositoryImpl transactions, so progress always matches the attempts.
 */
internal class ProgressRecorder(
    database: AppDatabase,
    private val timeProvider: TimeProvider,
    private val outbox: SyncOutboxWriter
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
        outbox.enqueue(userId, SyncEntityType.QUESTION_PROGRESS, update.progressId, SyncOperation.UPDATE, at)

        val chapterProgressId = chapterProgressDao.updateChapterProgress(
            userId = userId,
            chapterId = chapterId,
            attemptedIncrement = if (update.isFirstAttempt) 1 else 0,
            correctIncrement = if (update.isFirstCorrect) 1 else 0,
            practicedAt = at
        )
        outbox.enqueue(userId, SyncEntityType.CHAPTER_PROGRESS, chapterProgressId, SyncOperation.UPDATE, at)
    }

    suspend fun recordDailyActivity(userId: String, questionsAttempted: Int, timeSpentSeconds: Long, at: Long) {
        if (questionsAttempted == 0 && timeSpentSeconds == 0L) return
        val activityId = dailyActivityDao.updateDailyActivity(
            userId = userId,
            activityDate = timeProvider.localDate(at),
            questionsIncrement = questionsAttempted,
            timeIncrementSeconds = timeSpentSeconds,
            updatedAt = at
        )
        outbox.enqueue(userId, SyncEntityType.DAILY_ACTIVITY, activityId, SyncOperation.UPDATE, at)
    }
}
