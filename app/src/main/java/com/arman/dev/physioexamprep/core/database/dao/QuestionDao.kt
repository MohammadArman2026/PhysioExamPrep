package com.arman.dev.physioexamprep.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.relation.QuestionWithOptions
import kotlinx.coroutines.flow.Flow

/**
 * Questions have no display-order column, so lists use stable ID order.
 * All list and count queries return active questions only.
 */
@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE chapterId = :chapterId AND isActive = 1 ORDER BY id ASC")
    fun getQuestionsByChapter(chapterId: Long): Flow<List<QuestionEntity>>

    @Transaction
    @Query("SELECT * FROM questions WHERE id = :questionId")
    fun getQuestionWithOptions(questionId: Long): Flow<QuestionWithOptions?>

    @Query(
        "SELECT * FROM questions WHERE chapterId = :chapterId AND difficulty = :difficulty " +
            "AND isActive = 1 ORDER BY id ASC"
    )
    fun getQuestionsByDifficulty(chapterId: Long, difficulty: String): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions WHERE chapterId = :chapterId AND isActive = 1")
    fun getTotalQuestionsByChapter(chapterId: Long): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM questions q INNER JOIN chapters c ON q.chapterId = c.id " +
            "WHERE c.subjectId = :subjectId AND q.isActive = 1"
    )
    fun getTotalQuestionsBySubject(subjectId: Long): Flow<Int>

    /** Insert-or-update; never REPLACE (it would cascade-delete options). */
    @Upsert
    suspend fun upsertQuestions(questions: List<QuestionEntity>)

    /** Also deletes the question's options (CASCADE). */
    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)
}
