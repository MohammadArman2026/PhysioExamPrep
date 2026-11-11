package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.domain.model.MockType

/** A reusable mock-test configuration. Soft-deleted via [deletedAt]. */
@Entity(
    tableName = "mock_definitions",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class MockDefinitionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val mockType: MockType,
    /** Logical reference to an exam paper. No FK yet: there is no paper table. */
    val sourcePaperId: Long?,
    val durationMinutes: Int,
    val totalQuestions: Int,
    /** JSON describing the filters used to build a custom mock. */
    val filterConfig: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)
