package com.arman.dev.physioexamprep.core.database.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionResultEntity

data class AssessmentSessionWithResult(
    @Embedded
    val session: AssessmentSessionEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionId")
    val result: SessionResultEntity?
)
