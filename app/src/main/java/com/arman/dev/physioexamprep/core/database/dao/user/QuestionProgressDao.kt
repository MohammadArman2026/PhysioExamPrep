package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionProgressEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/** What changed when an attempt was recorded; used to update chapter progress. */
data class QuestionProgressUpdate(
    val progressId: String,
    val isFirstAttempt: Boolean,
    val isFirstCorrect: Boolean
)

@Dao
abstract class QuestionProgressDao {

    @Query("SELECT * FROM question_progress WHERE userId = :userId AND questionId = :questionId")
    abstract suspend fun getQuestionProgress(userId: String, questionId: Long): QuestionProgressEntity?

    @Query("SELECT * FROM question_progress WHERE userId = :userId AND questionId = :questionId")
    abstract fun observeQuestionProgress(userId: String, questionId: Long): Flow<QuestionProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertQuestionProgress(progress: QuestionProgressEntity)

    @Query(
        "UPDATE question_progress SET attemptCount = attemptCount + 1, " +
            "correctCount = correctCount + :correctIncrement, " +
            "incorrectCount = incorrectCount + :incorrectIncrement, " +
            "lastAttemptAt = :attemptedAt, updatedAt = :attemptedAt WHERE id = :progressId"
    )
    abstract suspend fun incrementQuestionProgress(
        progressId: String,
        correctIncrement: Int,
        incorrectIncrement: Int,
        attemptedAt: Long
    ): Int

    /**
     * Records one evaluated attempt. Read-then-write inside a transaction because
     * SQLite's INSERT ... ON CONFLICT DO UPDATE needs API 30+ (minSdk is 24).
     */
    @Transaction
    open suspend fun updateQuestionProgress(
        userId: String,
        questionId: Long,
        isCorrect: Boolean,
        attemptedAt: Long
    ): QuestionProgressUpdate {
        val existing = getQuestionProgress(userId, questionId)
        if (existing == null) {
            val id = UUID.randomUUID().toString()
            insertQuestionProgress(
                QuestionProgressEntity(
                    id = id,
                    userId = userId,
                    questionId = questionId,
                    attemptCount = 1,
                    correctCount = if (isCorrect) 1 else 0,
                    incorrectCount = if (isCorrect) 0 else 1,
                    lastAttemptAt = attemptedAt,
                    updatedAt = attemptedAt
                )
            )
            return QuestionProgressUpdate(id, isFirstAttempt = true, isFirstCorrect = isCorrect)
        }
        incrementQuestionProgress(
            progressId = existing.id,
            correctIncrement = if (isCorrect) 1 else 0,
            incorrectIncrement = if (isCorrect) 0 else 1,
            attemptedAt = attemptedAt
        )
        return QuestionProgressUpdate(
            progressId = existing.id,
            isFirstAttempt = existing.attemptCount == 0,
            isFirstCorrect = isCorrect && existing.correctCount == 0
        )
    }
}
