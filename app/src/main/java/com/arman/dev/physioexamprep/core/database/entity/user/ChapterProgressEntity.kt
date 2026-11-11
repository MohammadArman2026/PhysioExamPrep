package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity

/**
 * Per-user progress in one chapter.
 * [attemptedQuestions] = distinct questions attempted at least once;
 * [correctQuestions] = distinct questions answered correctly at least once.
 */
@Entity(
    tableName = "chapter_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "chapterId"], unique = true),
        Index(value = ["chapterId"])
    ]
)
data class ChapterProgressEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val chapterId: Long,
    val attemptedQuestions: Int = 0,
    val correctQuestions: Int = 0,
    val lastPracticedAt: Long?,
    val updatedAt: Long
)
