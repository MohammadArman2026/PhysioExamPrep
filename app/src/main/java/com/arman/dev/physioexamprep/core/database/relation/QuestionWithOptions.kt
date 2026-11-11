package com.arman.dev.physioexamprep.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity

data class QuestionWithOptions(
    @Embedded
    val question: QuestionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionId"
    )
    val options: List<QuestionOptionEntity>
) {
    /** Room does not order @Relation results, so use this for display (A, B, C, D...). */
    val sortedOptions: List<QuestionOptionEntity>
        get() = options.sortedWith(compareBy({ it.displayOrder }, { it.id }))

    /** True when more than one option is marked correct. */
    val hasMultipleCorrectAnswers: Boolean
        get() = options.count { it.isCorrect } > 1
}
