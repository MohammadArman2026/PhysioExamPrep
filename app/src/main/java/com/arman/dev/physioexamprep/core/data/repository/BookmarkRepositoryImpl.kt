package com.arman.dev.physioexamprep.core.data.repository

import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : BookmarkRepository {

    private val dao = database.questionBookmarkDao()

    override fun isBookmarked(userId: String, questionId: Long): Flow<Boolean> = dao.isBookmarked(userId, questionId)

    override fun observeBookmarkedQuestionIds(userId: String): Flow<List<Long>> =
        dao.observeBookmarkedQuestionIds(userId)

    override suspend fun addBookmark(userId: String, questionId: Long) {
        // Inserts, restores a removed bookmark, or does nothing if already bookmarked.
        dao.addBookmark(userId, questionId, timeProvider.nowMillis())
    }

    override suspend fun removeBookmark(userId: String, questionId: Long) {
        dao.removeBookmark(userId, questionId, timeProvider.nowMillis())
    }
}
