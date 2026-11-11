package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity
import com.arman.dev.physioexamprep.core.database.relation.user.SessionCorrectOptionRow
import com.arman.dev.physioexamprep.core.database.relation.user.SessionQuestionEvaluationRow
import com.arman.dev.physioexamprep.core.database.relation.user.SessionQuestionWithAnswer
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionQuestionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSessionQuestions(questions: List<SessionQuestionEntity>)

    @Query("SELECT * FROM session_questions WHERE id = :sessionQuestionId")
    suspend fun getSessionQuestionById(sessionQuestionId: String): SessionQuestionEntity?

    @Query("SELECT * FROM session_questions WHERE sessionId = :sessionId ORDER BY position ASC")
    suspend fun getQuestionsForSession(sessionId: String): List<SessionQuestionEntity>

    @Transaction
    @Query("SELECT * FROM session_questions WHERE sessionId = :sessionId ORDER BY position ASC")
    fun observeQuestionsForSession(sessionId: String): Flow<List<SessionQuestionWithAnswer>>

    // --- Evaluation helpers (read-only joins with content tables) ---

    @Query(
        "SELECT sq.id AS sessionQuestionId, sq.questionId AS questionId, q.chapterId AS chapterId, " +
            "q.questionType AS questionType, sq.marks AS marks, sq.negativeMarks AS negativeMarks " +
            "FROM session_questions sq INNER JOIN questions q ON q.id = sq.questionId " +
            "WHERE sq.sessionId = :sessionId ORDER BY sq.position ASC"
    )
    suspend fun getEvaluationRows(sessionId: String): List<SessionQuestionEvaluationRow>

    @Query(
        "SELECT sq.id AS sessionQuestionId, sq.questionId AS questionId, q.chapterId AS chapterId, " +
            "q.questionType AS questionType, sq.marks AS marks, sq.negativeMarks AS negativeMarks " +
            "FROM session_questions sq INNER JOIN questions q ON q.id = sq.questionId " +
            "WHERE sq.id = :sessionQuestionId"
    )
    suspend fun getEvaluationRow(sessionQuestionId: String): SessionQuestionEvaluationRow?

    @Query(
        "SELECT sq.id AS sessionQuestionId, o.id AS optionId " +
            "FROM session_questions sq INNER JOIN question_options o ON o.questionId = sq.questionId " +
            "WHERE sq.sessionId = :sessionId AND o.isCorrect = 1"
    )
    suspend fun getCorrectOptionsForSession(sessionId: String): List<SessionCorrectOptionRow>

    @Query("SELECT id FROM question_options WHERE questionId = :questionId AND isCorrect = 1")
    suspend fun getCorrectOptionIds(questionId: Long): List<Long>

    @Query("SELECT id FROM question_options WHERE questionId = :questionId")
    suspend fun getOptionIdsForQuestion(questionId: Long): List<Long>
}
