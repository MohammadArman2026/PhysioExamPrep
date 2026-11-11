package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity

/**
 * One selected option of an answer: one row for single-select, several rows
 * for multiple-select. The content FK uses RESTRICT to protect attempt history.
 */
@Entity(
    tableName = "session_answer_options",
    foreignKeys = [
        ForeignKey(
            entity = SessionAnswerEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionAnswerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionOptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["selectedOptionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["sessionAnswerId", "selectedOptionId"], unique = true),
        Index(value = ["selectedOptionId"])
    ]
)
data class SessionAnswerOptionEntity(
    @PrimaryKey
    val id: String,
    val sessionAnswerId: String,
    val selectedOptionId: Long
)
