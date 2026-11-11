package com.arman.dev.physioexamprep.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionOptionDao {

    @Query("SELECT * FROM question_options WHERE questionId = :questionId ORDER BY displayOrder ASC, id ASC")
    fun getOptionsByQuestion(questionId: Long): Flow<List<QuestionOptionEntity>>

    @Upsert
    suspend fun upsertOptions(options: List<QuestionOptionEntity>)

    /** Removes all options of a question, e.g. before re-importing revised options. */
    @Query("DELETE FROM question_options WHERE questionId = :questionId")
    suspend fun deleteOptionsByQuestion(questionId: Long)
}
