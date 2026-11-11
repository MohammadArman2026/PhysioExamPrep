package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.relation.user.AssessmentSessionWithQuestions
import com.arman.dev.physioexamprep.core.database.relation.user.AssessmentSessionWithResult
import com.arman.dev.physioexamprep.core.domain.model.SessionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentSessionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createSession(session: AssessmentSessionEntity)

    @Query("SELECT * FROM assessment_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): AssessmentSessionEntity?

    @Transaction
    @Query("SELECT * FROM assessment_sessions WHERE id = :sessionId AND deletedAt IS NULL")
    fun observeSessionWithQuestions(sessionId: String): Flow<AssessmentSessionWithQuestions?>

    @Query(
        "SELECT * FROM assessment_sessions WHERE userId = :userId AND status = 'IN_PROGRESS' " +
            "AND deletedAt IS NULL ORDER BY startedAt DESC"
    )
    fun observeActiveSessions(userId: String): Flow<List<AssessmentSessionEntity>>

    @Query(
        "SELECT * FROM assessment_sessions WHERE userId = :userId AND deletedAt IS NULL " +
            "ORDER BY startedAt DESC"
    )
    fun observeSessions(userId: String): Flow<List<AssessmentSessionEntity>>

    @Query("UPDATE assessment_sessions SET status = :status, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateSessionStatus(sessionId: String, status: SessionStatus, updatedAt: Long): Int

    @Query(
        "UPDATE assessment_sessions SET currentQuestionIndex = :index, updatedAt = :updatedAt " +
            "WHERE id = :sessionId AND status = 'IN_PROGRESS'"
    )
    suspend fun updateCurrentQuestionIndex(sessionId: String, index: Int, updatedAt: Long): Int

    /** Only an IN_PROGRESS session can be completed; returns 0 otherwise. */
    @Query(
        "UPDATE assessment_sessions SET status = 'COMPLETED', completedAt = :completedAt, " +
            "updatedAt = :completedAt WHERE id = :sessionId AND status = 'IN_PROGRESS'"
    )
    suspend fun completeSession(sessionId: String, completedAt: Long): Int

    @Transaction
    @Query("SELECT * FROM assessment_sessions WHERE id = :sessionId AND deletedAt IS NULL")
    fun observeSessionWithResult(sessionId: String): Flow<AssessmentSessionWithResult?>

    @Transaction
    @Query(
        "SELECT * FROM assessment_sessions WHERE userId = :userId AND status = 'COMPLETED' " +
            "AND deletedAt IS NULL ORDER BY completedAt DESC"
    )
    fun observeCompletedSessionsWithResults(userId: String): Flow<List<AssessmentSessionWithResult>>
}
