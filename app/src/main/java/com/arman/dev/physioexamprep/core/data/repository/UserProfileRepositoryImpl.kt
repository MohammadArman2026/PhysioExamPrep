package com.arman.dev.physioexamprep.core.data.repository

import androidx.room.withTransaction
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.data.mapper.toDomain
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import com.arman.dev.physioexamprep.core.domain.model.UserProfile
import com.arman.dev.physioexamprep.core.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : UserProfileRepository {

    private val dao = database.userProfileDao()
    private val outbox = SyncOutboxWriter(database.syncOutboxDao())

    override fun observeProfile(userId: String): Flow<UserProfile?> =
        dao.observeProfile(userId).map { it?.toDomain() }

    override suspend fun getProfile(userId: String): UserProfile? = dao.getProfileById(userId)?.toDomain()

    override suspend fun saveProfile(
        userId: String,
        displayName: String,
        email: String?,
        targetExam: String?,
        dailyQuestionGoal: Int
    ): UserProfile {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(dailyQuestionGoal > 0) { "Daily goal must be positive" }
        return database.withTransaction {
            val now = timeProvider.nowMillis()
            val existing = dao.getProfileById(userId)
            val entity = UserProfileEntity(
                userId = userId,
                displayName = displayName,
                email = email,
                targetExam = targetExam,
                dailyQuestionGoal = dailyQuestionGoal,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
            dao.insertOrUpdateProfile(entity)
            outbox.enqueue(
                userId, SyncEntityType.USER_PROFILE, userId,
                if (existing == null) SyncOperation.INSERT else SyncOperation.UPDATE, now
            )
            entity.toDomain()
        }
    }
}
