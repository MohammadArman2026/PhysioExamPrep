package com.arman.dev.physioexamprep.core.domain.repository

import com.arman.dev.physioexamprep.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun getProfile(userId: String): UserProfile?

    /**
     * Creates the profile, or updates it if it exists (createdAt is preserved).
     * Must be called (e.g. after sign-in) before any other user data is written,
     * because every user table has a foreign key to the profile.
     */
    suspend fun saveProfile(
        userId: String,
        displayName: String,
        email: String?,
        targetExam: String?,
        dailyQuestionGoal: Int = 20
    ): UserProfile
}
