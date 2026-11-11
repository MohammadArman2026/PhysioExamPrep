package com.arman.dev.physioexamprep.core.database.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity

/**
 * A session with its question list and answer state. Question text/options are
 * NOT loaded here; fetch them per question through the content DAOs when shown.
 */
data class AssessmentSessionWithQuestions(
    @Embedded
    val session: AssessmentSessionEntity,
    @Relation(
        entity = SessionQuestionEntity::class,
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val questions: List<SessionQuestionWithAnswer>
) {
    /** Room does not order @Relation results. */
    val sortedQuestions: List<SessionQuestionWithAnswer>
        get() = questions.sortedBy { it.sessionQuestion.position }
}
