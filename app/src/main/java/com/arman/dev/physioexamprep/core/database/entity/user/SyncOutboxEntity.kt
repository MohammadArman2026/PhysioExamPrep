package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation

/**
 * A local change waiting to be pushed to the cloud. Only the queue exists for now;
 * nothing consumes it yet.
 */
@Entity(
    tableName = "sync_outbox",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "createdAt"]),
        Index(value = ["entityType", "entityId"])
    ]
)
data class SyncOutboxEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val entityType: SyncEntityType,
    val entityId: String,
    val operation: SyncOperation,
    val createdAt: Long,
    val retryCount: Int = 0
)
