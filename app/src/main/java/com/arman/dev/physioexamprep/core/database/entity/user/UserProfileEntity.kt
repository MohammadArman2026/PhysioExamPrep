package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Profile and study preferences of the signed-in user.
 * [userId] is the authentication provider's UID. No passwords or auth secrets are stored.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,
    val displayName: String,
    val email: String?,
    val targetExam: String?,
    val dailyQuestionGoal: Int = 20,
    val createdAt: Long,
    val updatedAt: Long
)
