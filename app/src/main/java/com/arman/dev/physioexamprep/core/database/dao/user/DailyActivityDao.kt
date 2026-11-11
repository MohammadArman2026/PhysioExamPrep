package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.DailyActivityEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class DailyActivityDao {

    @Query("SELECT * FROM daily_activity WHERE userId = :userId AND activityDate = :activityDate")
    abstract suspend fun getActivityForDate(userId: String, activityDate: String): DailyActivityEntity?

    /** Days on or after [fromDate] (YYYY-MM-DD), newest first. */
    @Query(
        "SELECT * FROM daily_activity WHERE userId = :userId AND activityDate >= :fromDate " +
            "ORDER BY activityDate DESC"
    )
    abstract fun observeActivityHistory(userId: String, fromDate: String): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertDailyActivity(activity: DailyActivityEntity)

    @Query(
        "UPDATE daily_activity SET questionsAttempted = questionsAttempted + :questionsIncrement, " +
            "timeSpentSeconds = timeSpentSeconds + :timeIncrementSeconds, updatedAt = :updatedAt " +
            "WHERE id = :activityId"
    )
    abstract suspend fun incrementDailyActivity(
        activityId: String,
        questionsIncrement: Int,
        timeIncrementSeconds: Long,
        updatedAt: Long
    ): Int

    /** Adds to a day's counters, creating the row if needed. Returns the row id. */
    @Transaction
    open suspend fun updateDailyActivity(
        userId: String,
        activityDate: String,
        questionsIncrement: Int,
        timeIncrementSeconds: Long,
        updatedAt: Long
    ): String {
        val existing = getActivityForDate(userId, activityDate)
        if (existing == null) {
            val id = UUID.randomUUID().toString()
            insertDailyActivity(
                DailyActivityEntity(
                    id = id,
                    userId = userId,
                    activityDate = activityDate,
                    questionsAttempted = questionsIncrement,
                    timeSpentSeconds = timeIncrementSeconds,
                    updatedAt = updatedAt
                )
            )
            return id
        }
        incrementDailyActivity(existing.id, questionsIncrement, timeIncrementSeconds, updatedAt)
        return existing.id
    }
}
