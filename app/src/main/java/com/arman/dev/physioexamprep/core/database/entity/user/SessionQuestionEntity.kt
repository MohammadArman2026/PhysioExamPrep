package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity

/**
 * The fixed, ordered question list of a session. Marks are copied here when the
 * session starts so later mock/content edits don't change an attempt.
 * The content FK uses RESTRICT to protect attempt history (retire content with isActive).
 */
@Entity(
    tableName = "session_questions",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["sessionId", "position"], unique = true),
        Index(value = ["sessionId", "questionId"], unique = true),
        Index(value = ["questionId"])
    ]
)
data class SessionQuestionEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val questionId: Long,
    val position: Int,
    val marks: Double,
    val negativeMarks: Double
)
