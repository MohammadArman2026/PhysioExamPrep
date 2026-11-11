package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    /**
     * Insert-or-update by primary key. @Upsert UPDATEs an existing row, unlike
     * REPLACE, which would delete it and cascade-delete all of the user's data.
     */
    @Upsert
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getProfileById(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun observeProfile(userId: String): Flow<UserProfileEntity?>
}
