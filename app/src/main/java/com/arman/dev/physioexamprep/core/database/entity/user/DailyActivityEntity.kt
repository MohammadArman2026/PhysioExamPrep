package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Study activity for one local calendar day. */
@Entity(
    tableName = "daily_activity",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "activityDate"], unique = true)]
)
data class DailyActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    /** Local date, formatted YYYY-MM-DD (sorts correctly as text). */
    val activityDate: String,
    val questionsAttempted: Int = 0,
    val timeSpentSeconds: Long = 0,
    val updatedAt: Long
)
