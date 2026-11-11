package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.ChapterProgressEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class ChapterProgressDao {

    @Query("SELECT * FROM chapter_progress WHERE userId = :userId AND chapterId = :chapterId")
    abstract suspend fun getChapterProgress(userId: String, chapterId: Long): ChapterProgressEntity?

    @Query("SELECT * FROM chapter_progress WHERE userId = :userId AND chapterId = :chapterId")
    abstract fun observeChapterProgress(userId: String, chapterId: Long): Flow<ChapterProgressEntity?>

    /** Progress rows for the chapters of one subject, in chapter display order. */
    @Query(
        "SELECT cp.* FROM chapter_progress cp INNER JOIN chapters c ON c.id = cp.chapterId " +
            "WHERE cp.userId = :userId AND c.subjectId = :subjectId ORDER BY c.displayOrder ASC, c.id ASC"
    )
    abstract fun observeChapterProgressForSubject(userId: String, subjectId: Long): Flow<List<ChapterProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertChapterProgress(progress: ChapterProgressEntity)

    @Query(
        "UPDATE chapter_progress SET attemptedQuestions = attemptedQuestions + :attemptedIncrement, " +
            "correctQuestions = correctQuestions + :correctIncrement, " +
            "lastPracticedAt = :practicedAt, updatedAt = :practicedAt WHERE id = :progressId"
    )
    abstract suspend fun incrementChapterProgress(
        progressId: String,
        attemptedIncrement: Int,
        correctIncrement: Int,
        practicedAt: Long
    ): Int

    /** Adds to a chapter's counters, creating the row if needed. Returns the row id. */
    @Transaction
    open suspend fun updateChapterProgress(
        userId: String,
        chapterId: Long,
        attemptedIncrement: Int,
        correctIncrement: Int,
        practicedAt: Long
    ): String {
        val existing = getChapterProgress(userId, chapterId)
        if (existing == null) {
            val id = UUID.randomUUID().toString()
            insertChapterProgress(
                ChapterProgressEntity(
                    id = id,
                    userId = userId,
                    chapterId = chapterId,
                    attemptedQuestions = attemptedIncrement,
                    correctQuestions = correctIncrement,
                    lastPracticedAt = practicedAt,
                    updatedAt = practicedAt
                )
            )
            return id
        }
        incrementChapterProgress(existing.id, attemptedIncrement, correctIncrement, practicedAt)
        return existing.id
    }
}
