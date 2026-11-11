package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity

/** Per-user performance on one question. */
@Entity(
    tableName = "question_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "questionId"], unique = true),
        Index(value = ["questionId"])
    ]
)
data class QuestionProgressEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val questionId: Long,
    val attemptCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastAttemptAt: Long?,
    val updatedAt: Long
)
