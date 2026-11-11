package com.arman.dev.physioexamprep.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun isBookmarked(userId: String, questionId: Long): Flow<Boolean>

    /** Most recently bookmarked first. Load question content via the content DAOs. */
    fun observeBookmarkedQuestionIds(userId: String): Flow<List<Long>>

    suspend fun addBookmark(userId: String, questionId: Long)
    suspend fun removeBookmark(userId: String, questionId: Long)
}
