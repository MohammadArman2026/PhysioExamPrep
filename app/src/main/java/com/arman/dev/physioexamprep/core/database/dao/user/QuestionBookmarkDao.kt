package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionBookmarkEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class QuestionBookmarkDao {

    /** Includes soft-deleted bookmarks. */
    @Query("SELECT * FROM question_bookmarks WHERE userId = :userId AND questionId = :questionId")
    abstract suspend fun getBookmark(userId: String, questionId: Long): QuestionBookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertBookmark(bookmark: QuestionBookmarkEntity)

    @Query("UPDATE question_bookmarks SET deletedAt = NULL, updatedAt = :now WHERE id = :bookmarkId")
    abstract suspend fun restoreBookmark(bookmarkId: String, now: Long): Int

    /**
     * Adds a bookmark, or restores a previously removed one (the unique
     * (userId, questionId) row is reused). Returns the bookmark id.
     */
    @Transaction
    open suspend fun addBookmark(userId: String, questionId: Long, now: Long): String {
        val existing = getBookmark(userId, questionId)
        return when {
            existing == null -> {
                val id = UUID.randomUUID().toString()
                insertBookmark(
                    QuestionBookmarkEntity(
                        id = id,
                        userId = userId,
                        questionId = questionId,
                        createdAt = now,
                        updatedAt = now,
                        deletedAt = null
                    )
                )
                id
            }
            existing.deletedAt != null -> {
                restoreBookmark(existing.id, now)
                existing.id
            }
            else -> existing.id
        }
    }

    /** Soft delete; returns 0 if the question wasn't bookmarked. */
    @Query(
        "UPDATE question_bookmarks SET deletedAt = :now, updatedAt = :now " +
            "WHERE userId = :userId AND questionId = :questionId AND deletedAt IS NULL"
    )
    abstract suspend fun removeBookmark(userId: String, questionId: Long, now: Long): Int

    @Query(
        "SELECT EXISTS(SELECT 1 FROM question_bookmarks " +
            "WHERE userId = :userId AND questionId = :questionId AND deletedAt IS NULL)"
    )
    abstract fun isBookmarked(userId: String, questionId: Long): Flow<Boolean>

    @Query(
        "SELECT questionId FROM question_bookmarks WHERE userId = :userId AND deletedAt IS NULL " +
            "ORDER BY updatedAt DESC"
    )
    abstract fun observeBookmarkedQuestionIds(userId: String): Flow<List<Long>>

    /** Bookmarked questions only (no options), most recently bookmarked first. */
    @Query(
        "SELECT q.* FROM questions q INNER JOIN question_bookmarks b ON b.questionId = q.id " +
            "WHERE b.userId = :userId AND b.deletedAt IS NULL ORDER BY b.updatedAt DESC"
    )
    abstract fun observeBookmarkedQuestions(userId: String): Flow<List<QuestionEntity>>
}
