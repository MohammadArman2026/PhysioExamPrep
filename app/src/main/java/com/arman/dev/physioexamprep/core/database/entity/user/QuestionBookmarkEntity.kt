package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity

/** A bookmarked question. Removing a bookmark soft-deletes it via [deletedAt]. */
@Entity(
    tableName = "question_bookmarks",
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
data class QuestionBookmarkEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val questionId: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)
