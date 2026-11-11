package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionQuestionEntity
import com.arman.dev.physioexamprep.core.database.relation.user.MockDefinitionWithQuestions
import kotlinx.coroutines.flow.Flow

@Dao
interface MockDefinitionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMockDefinition(mockDefinition: MockDefinitionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMockQuestions(questions: List<MockDefinitionQuestionEntity>)

    /** Returns null for unknown or soft-deleted definitions. */
    @Transaction
    @Query("SELECT * FROM mock_definitions WHERE id = :id AND deletedAt IS NULL")
    suspend fun getMockDefinition(id: String): MockDefinitionWithQuestions?

    @Transaction
    @Query("SELECT * FROM mock_definitions WHERE id = :id AND deletedAt IS NULL")
    fun observeMockDefinition(id: String): Flow<MockDefinitionWithQuestions?>

    /** Includes soft-deleted rows. */
    @Query("SELECT * FROM mock_definitions WHERE id = :id")
    suspend fun getMockDefinitionEntity(id: String): MockDefinitionEntity?

    @Query(
        "SELECT * FROM mock_definitions WHERE userId = :userId AND deletedAt IS NULL " +
            "ORDER BY updatedAt DESC, id ASC"
    )
    fun observeMockDefinitions(userId: String): Flow<List<MockDefinitionEntity>>

    /** Soft delete (keeps the row so past sessions still reference it). */
    @Query(
        "UPDATE mock_definitions SET deletedAt = :deletedAt, updatedAt = :deletedAt " +
            "WHERE id = :id AND deletedAt IS NULL"
    )
    suspend fun softDeleteMockDefinition(id: String, deletedAt: Long): Int

    /**
     * Permanent delete. Its questions are cascade-deleted and
     * sessions that used it keep their history with mockDefinitionId set to NULL.
     */
    @Query("DELETE FROM mock_definitions WHERE id = :id")
    suspend fun deleteMockDefinition(id: String): Int
}
