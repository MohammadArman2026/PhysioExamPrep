package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Snapshot of a completed session's score. Computed once at completion and never
 * recalculated, so later content changes don't alter historical results.
 */
@Entity(
    tableName = "session_results",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SessionResultEntity(
    @PrimaryKey
    val sessionId: String,
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unattemptedQuestions: Int,
    val totalMarks: Double,
    val obtainedMarks: Double,
    /** Percentage (0–100) of attempted questions answered correctly. */
    val accuracy: Double,
    val timeTakenSeconds: Long,
    val completedAt: Long
)
