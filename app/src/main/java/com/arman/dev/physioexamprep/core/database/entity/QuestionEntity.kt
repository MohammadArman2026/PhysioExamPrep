package com.arman.dev.physioexamprep.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A question inside a [ChapterEntity]. Its answer choices live in
 * [QuestionOptionEntity]. Deleting a chapter deletes its questions.
 */
@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chapterId"]),
        Index(value = ["difficulty"]),
        Index(value = ["isActive"])
    ]
)
data class QuestionEntity(
    @PrimaryKey
    val id: Long,
    val chapterId: Long,
    val questionText: String,
    /** See [QuestionType]. */
    val questionType: String,
    /** See [Difficulty]. */
    val difficulty: String,
    val explanation: String?,
    /** Only set for questions that include a diagram or image. */
    val imagePath: String?,
    /** Bumped whenever the question's content is revised. */
    val contentVersion: Int,
    /** Epoch millis of the last content change. */
    val updatedAt: Long,
    val isActive: Boolean = true
) {
    /** Allowed values for [questionType]. */
    object QuestionType {
        const val SINGLE_CORRECT = "SINGLE_CORRECT"
        const val MULTIPLE_CORRECT = "MULTIPLE_CORRECT"
    }

    /** Allowed values for [difficulty]. */
    object Difficulty {
        const val EASY = "EASY"
        const val MEDIUM = "MEDIUM"
        const val HARD = "HARD"
    }
}
