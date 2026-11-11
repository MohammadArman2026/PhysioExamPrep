package com.arman.dev.physioexamprep.core.data.repository

import com.arman.dev.physioexamprep.core.database.dao.user.SyncOutboxDao
import com.arman.dev.physioexamprep.core.database.entity.user.SyncOutboxEntity
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import java.util.UUID

/**
 * Queues a change for future cloud sync. Call it inside the same transaction as the
 * change it describes. An UPDATE is skipped when the entity's latest pending
 * operation is an INSERT or UPDATE (the sync worker reads the row's current state).
 */
internal class SyncOutboxWriter(private val syncOutboxDao: SyncOutboxDao) {

    suspend fun enqueue(
        userId: String,
        entityType: SyncEntityType,
        entityId: String,
        operation: SyncOperation,
        now: Long
    ) {
        if (operation == SyncOperation.UPDATE) {
            val latest = syncOutboxDao.getLatestPendingOperation(entityType, entityId)?.operation
            // Coalesce into a pending INSERT/UPDATE. Never after a DELETE, or a re-created
            // row (e.g. restored bookmark) would end up deleted remotely.
            if (latest == SyncOperation.INSERT || latest == SyncOperation.UPDATE) return
        }
        syncOutboxDao.insertPendingOperation(
            SyncOutboxEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                entityType = entityType,
                entityId = entityId,
                operation = operation,
                createdAt = now
            )
        )
    }
}
