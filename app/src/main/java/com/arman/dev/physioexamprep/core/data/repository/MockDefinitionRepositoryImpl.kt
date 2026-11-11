package com.arman.dev.physioexamprep.core.data.repository

import androidx.room.withTransaction
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.data.mapper.toDomain
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionQuestionEntity
import com.arman.dev.physioexamprep.core.domain.model.MockDefinition
import com.arman.dev.physioexamprep.core.domain.model.MockType
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import com.arman.dev.physioexamprep.core.domain.repository.MockDefinitionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MockDefinitionRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : MockDefinitionRepository {

    private val dao = database.mockDefinitionDao()
    private val outbox = SyncOutboxWriter(database.syncOutboxDao())

    override fun observeMockDefinitions(userId: String): Flow<List<MockDefinition>> =
        dao.observeMockDefinitions(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun getMockDefinition(id: String): MockDefinition? = dao.getMockDefinition(id)?.toDomain()

    override suspend fun createMockDefinition(
        userId: String,
        title: String,
        description: String?,
        mockType: MockType,
        sourcePaperId: Long?,
        durationMinutes: Int,
        questions: List<QuestionSelection>,
        filterConfig: String?
    ): String {
        require(questions.isNotEmpty()) { "A mock needs at least one question" }
        return database.withTransaction {
            val now = timeProvider.nowMillis()
            val id = UUID.randomUUID().toString()
            dao.insertMockDefinition(
                MockDefinitionEntity(
                    id = id,
                    userId = userId,
                    title = title,
                    description = description,
                    mockType = mockType,
                    sourcePaperId = sourcePaperId,
                    durationMinutes = durationMinutes,
                    totalQuestions = questions.size,
                    filterConfig = filterConfig,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null
                )
            )
            dao.insertMockQuestions(
                questions.mapIndexed { index, q ->
                    MockDefinitionQuestionEntity(
                        id = UUID.randomUUID().toString(),
                        mockDefinitionId = id,
                        questionId = q.questionId,
                        position = index,
                        marks = q.marks,
                        negativeMarks = q.negativeMarks
                    )
                }
            )
            outbox.enqueue(userId, SyncEntityType.MOCK_DEFINITION, id, SyncOperation.INSERT, now)
            id
        }
    }

    override suspend fun deleteMockDefinition(id: String) {
        database.withTransaction<Unit> {
            val existing = dao.getMockDefinitionEntity(id) ?: return@withTransaction
            val now = timeProvider.nowMillis()
            if (dao.softDeleteMockDefinition(id, now) == 1) {
                outbox.enqueue(existing.userId, SyncEntityType.MOCK_DEFINITION, id, SyncOperation.DELETE, now)
            }
        }
    }
}
