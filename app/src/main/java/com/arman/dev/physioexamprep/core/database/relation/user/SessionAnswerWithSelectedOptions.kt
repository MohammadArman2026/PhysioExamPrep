package com.arman.dev.physioexamprep.core.database.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerOptionEntity

data class SessionAnswerWithSelectedOptions(
    @Embedded
    val answer: SessionAnswerEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionAnswerId")
    val selectedOptions: List<SessionAnswerOptionEntity>
) {
    val selectedOptionIds: Set<Long>
        get() = selectedOptions.mapTo(mutableSetOf()) { it.selectedOptionId }
}
