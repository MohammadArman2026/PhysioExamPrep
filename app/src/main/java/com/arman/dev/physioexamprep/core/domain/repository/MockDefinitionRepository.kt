package com.arman.dev.physioexamprep.core.domain.repository

import com.arman.dev.physioexamprep.core.domain.model.MockDefinition
import com.arman.dev.physioexamprep.core.domain.model.MockType
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import kotlinx.coroutines.flow.Flow

interface MockDefinitionRepository {
    /** Summaries (without questions), most recently updated first. */
    fun observeMockDefinitions(userId: String): Flow<List<MockDefinition>>

    /** With questions in position order; null if missing or deleted. */
    suspend fun getMockDefinition(id: String): MockDefinition?

    /** Saves the definition and its questions atomically. Returns the new id. */
    suspend fun createMockDefinition(
        userId: String,
        title: String,
        description: String?,
        mockType: MockType,
        sourcePaperId: Long?,
        durationMinutes: Int,
        questions: List<QuestionSelection>,
        filterConfig: String?
    ): String

    /** Soft delete. */
    suspend fun deleteMockDefinition(id: String)
}
