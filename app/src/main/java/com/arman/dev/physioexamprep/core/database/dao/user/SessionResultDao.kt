package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arman.dev.physioexamprep.core.database.entity.user.SessionResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionResultDao {

    /** ABORT: a result snapshot is written once and never overwritten. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertResult(result: SessionResultEntity)

    @Query("SELECT * FROM session_results WHERE sessionId = :sessionId")
    suspend fun getResultBySessionId(sessionId: String): SessionResultEntity?

    @Query("SELECT * FROM session_results WHERE sessionId = :sessionId")
    fun observeResult(sessionId: String): Flow<SessionResultEntity?>

    @Query(
        "SELECT r.* FROM session_results r INNER JOIN assessment_sessions s ON s.id = r.sessionId " +
            "WHERE s.userId = :userId AND s.deletedAt IS NULL ORDER BY r.completedAt DESC"
    )
    fun observeResults(userId: String): Flow<List<SessionResultEntity>>
}
