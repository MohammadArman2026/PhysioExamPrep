package com.arman.dev.physioexamprep.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One answer choice of a [QuestionEntity].
 *
 * Correctness is stored per option, so a question can have one correct option
 * (single-correct) or several (multiple-correct).
 * Deleting a question deletes its options.
 */
@Entity(
    tableName = "question_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questionId"])]
)
data class QuestionOptionEntity(
    @PrimaryKey
    val id: Long,
    val questionId: Long,
    val optionText: String,
    val isCorrect: Boolean,
    /** Position of the option under its question (ascending): A, B, C, D... */
    val displayOrder: Int
)
