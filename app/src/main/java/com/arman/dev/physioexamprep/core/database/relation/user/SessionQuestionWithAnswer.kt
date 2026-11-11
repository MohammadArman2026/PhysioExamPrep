package com.arman.dev.physioexamprep.core.database.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity

data class SessionQuestionWithAnswer(
    @Embedded
    val sessionQuestion: SessionQuestionEntity,
    @Relation(
        entity = SessionAnswerEntity::class,
        parentColumn = "id",
        entityColumn = "sessionQuestionId"
    )
    val answer: SessionAnswerWithSelectedOptions?
)
