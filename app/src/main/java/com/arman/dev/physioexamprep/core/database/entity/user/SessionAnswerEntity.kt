package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus

/**
 * Answer state of one session question. The unique index on [sessionQuestionId]
 * guarantees at most one answer row per session question.
 */
@Entity(
    tableName = "session_answers",
    foreignKeys = [
        ForeignKey(
            entity = SessionQuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionQuestionId"], unique = true)]
)
data class SessionAnswerEntity(
    @PrimaryKey
    val id: String,
    val sessionQuestionId: String,
    val answerStatus: AnswerStatus,
    val isMarkedForReview: Boolean = false,
    val isVisited: Boolean = false,
    val timeSpentSeconds: Long = 0,
    val answeredAt: Long?,
    val updatedAt: Long
)
