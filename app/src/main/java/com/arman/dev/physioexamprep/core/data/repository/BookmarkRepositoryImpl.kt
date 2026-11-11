package com.arman.dev.physioexamprep.core.data.repository

import androidx.room.withTransaction
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import com.arman.dev.physioexamprep.core.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : BookmarkRepository {

    private val dao = database.questionBookmarkDao()
    private val outbox = SyncOutboxWriter(database.syncOutboxDao())

    override fun isBookmarked(userId: String, questionId: Long): Flow<Boolean> = dao.isBookmarked(userId, questionId)

    override fun observeBookmarkedQuestionIds(userId: String): Flow<List<Long>> =
        dao.observeBookmarkedQuestionIds(userId)

    override suspend fun addBookmark(userId: String, questionId: Long) {
        database.withTransaction<Unit> {
            val now = timeProvider.nowMillis()
            val before = dao.getBookmark(userId, questionId)
            if (before != null && before.deletedAt == null) return@withTransaction // already bookmarked
            val id = dao.addBookmark(userId, questionId, now)
            outbox.enqueue(
                userId, SyncEntityType.QUESTION_BOOKMARK, id,
                if (before == null) SyncOperation.INSERT else SyncOperation.UPDATE, now
            )
        }
    }

    override suspend fun removeBookmark(userId: String, questionId: Long) {
        database.withTransaction<Unit> {
            val now = timeProvider.nowMillis()
            val existing = dao.getBookmark(userId, questionId) ?: return@withTransaction
            if (dao.removeBookmark(userId, questionId, now) == 1) {
                outbox.enqueue(userId, SyncEntityType.QUESTION_BOOKMARK, existing.id, SyncOperation.DELETE, now)
            }
        }
    }
}
