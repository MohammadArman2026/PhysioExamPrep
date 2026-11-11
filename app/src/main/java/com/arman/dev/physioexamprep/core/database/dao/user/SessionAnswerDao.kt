package com.arman.dev.physioexamprep.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerOptionEntity
import com.arman.dev.physioexamprep.core.database.relation.user.SessionAnswerWithSelectedOptions
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class SessionAnswerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertAnswers(answers: List<SessionAnswerEntity>)

    @Query("SELECT * FROM session_answers WHERE sessionQuestionId = :sessionQuestionId")
    abstract suspend fun getAnswerEntity(sessionQuestionId: String): SessionAnswerEntity?

    @Transaction
    @Query("SELECT * FROM session_answers WHERE sessionQuestionId = :sessionQuestionId")
    abstract suspend fun getAnswerForSessionQuestion(sessionQuestionId: String): SessionAnswerWithSelectedOptions?

    @Transaction
    @Query(
        "SELECT a.* FROM session_answers a INNER JOIN session_questions sq ON sq.id = a.sessionQuestionId " +
            "WHERE sq.sessionId = :sessionId ORDER BY sq.position ASC"
    )
    abstract fun observeAnswersForSession(sessionId: String): Flow<List<SessionAnswerWithSelectedOptions>>

    @Transaction
    @Query(
        "SELECT a.* FROM session_answers a INNER JOIN session_questions sq ON sq.id = a.sessionQuestionId " +
            "WHERE sq.sessionId = :sessionId ORDER BY sq.position ASC"
    )
    abstract suspend fun getAnswersForSession(sessionId: String): List<SessionAnswerWithSelectedOptions>

    @Query("DELETE FROM session_answer_options WHERE sessionAnswerId = :sessionAnswerId")
    abstract suspend fun deleteSelectedOptions(sessionAnswerId: String)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertSelectedOptions(options: List<SessionAnswerOptionEntity>)

    @Query(
        "UPDATE session_answers SET answerStatus = :status, answeredAt = :answeredAt, isVisited = 1, " +
            "timeSpentSeconds = timeSpentSeconds + :additionalTimeSeconds, updatedAt = :updatedAt " +
            "WHERE id = :sessionAnswerId"
    )
    abstract suspend fun updateAnswerState(
        sessionAnswerId: String,
        status: AnswerStatus,
        answeredAt: Long?,
        additionalTimeSeconds: Long,
        updatedAt: Long
    ): Int

    /**
     * Atomically replaces the selected options of an answer. An empty selection
     * clears the answer (UNANSWERED). Validation (option belongs to the question,
     * single-select limit) is done by the repository before calling this.
     */
    @Transaction
    open suspend fun saveAnswer(
        sessionAnswerId: String,
        selectedOptionIds: Collection<Long>,
        additionalTimeSeconds: Long,
        now: Long
    ) {
        deleteSelectedOptions(sessionAnswerId)
        val distinctIds = selectedOptionIds.distinct()
        if (distinctIds.isNotEmpty()) {
            insertSelectedOptions(
                distinctIds.map { optionId ->
                    SessionAnswerOptionEntity(
                        id = UUID.randomUUID().toString(),
                        sessionAnswerId = sessionAnswerId,
                        selectedOptionId = optionId
                    )
                }
            )
        }
        val answered = distinctIds.isNotEmpty()
        val updated = updateAnswerState(
            sessionAnswerId = sessionAnswerId,
            status = if (answered) AnswerStatus.ANSWERED else AnswerStatus.UNANSWERED,
            answeredAt = if (answered) now else null,
            additionalTimeSeconds = additionalTimeSeconds,
            updatedAt = now
        )
        check(updated == 1) { "Session answer $sessionAnswerId does not exist" }
    }

    @Query(
        "UPDATE session_answers SET isMarkedForReview = :isMarkedForReview, updatedAt = :updatedAt " +
            "WHERE sessionQuestionId = :sessionQuestionId"
    )
    abstract suspend fun updateReviewStatus(sessionQuestionId: String, isMarkedForReview: Boolean, updatedAt: Long): Int

    @Query(
        "UPDATE session_answers SET isVisited = :isVisited, updatedAt = :updatedAt " +
            "WHERE sessionQuestionId = :sessionQuestionId"
    )
    abstract suspend fun updateVisitedStatus(sessionQuestionId: String, isVisited: Boolean, updatedAt: Long): Int

    @Query(
        "UPDATE session_answers SET timeSpentSeconds = timeSpentSeconds + :additionalTimeSeconds, " +
            "updatedAt = :updatedAt WHERE sessionQuestionId = :sessionQuestionId"
    )
    abstract suspend fun addTimeSpent(sessionQuestionId: String, additionalTimeSeconds: Long, updatedAt: Long): Int
}
