package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arman.dev.physioexamprep.core.database.entity.user.SyncOutboxEntity
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOutboxDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPendingOperation(operation: SyncOutboxEntity)

    /** Oldest first, in insertion order. */
    @Query("SELECT * FROM sync_outbox WHERE userId = :userId ORDER BY createdAt ASC, rowid ASC LIMIT :limit")
    suspend fun getPendingOperations(userId: String, limit: Int): List<SyncOutboxEntity>

    /** Most recently queued operation for an entity, or null if none is pending. */
    @Query(
        "SELECT * FROM sync_outbox WHERE entityType = :entityType AND entityId = :entityId " +
            "ORDER BY createdAt DESC, rowid DESC LIMIT 1"
    )
    suspend fun getLatestPendingOperation(entityType: SyncEntityType, entityId: String): SyncOutboxEntity?

    @Query("SELECT COUNT(*) FROM sync_outbox WHERE userId = :userId")
    fun observePendingCount(userId: String): Flow<Int>

    @Query("UPDATE sync_outbox SET retryCount = retryCount + 1 WHERE id = :operationId")
    suspend fun incrementRetryCount(operationId: String): Int

    @Query("DELETE FROM sync_outbox WHERE id = :operationId")
    suspend fun deleteSyncedOperation(operationId: String): Int
}
