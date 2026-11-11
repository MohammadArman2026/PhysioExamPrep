package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.domain.model.SessionStatus
import com.arman.dev.physioexamprep.core.domain.model.SessionType

/** One practice, PYQ or mock attempt. Soft-deleted via [deletedAt]. */
@Entity(
    tableName = "assessment_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MockDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["mockDefinitionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["startedAt"]),
        Index(value = ["mockDefinitionId"])
    ]
)
data class AssessmentSessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val sessionType: SessionType,
    val title: String,
    val mockDefinitionId: String?,
    /** Logical reference to an exam paper. No FK yet: there is no paper table. */
    val sourcePaperId: Long?,
    /** Time limit; null = untimed. */
    val durationSeconds: Long?,
    val startedAt: Long,
    val completedAt: Long?,
    val currentQuestionIndex: Int = 0,
    val status: SessionStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)
