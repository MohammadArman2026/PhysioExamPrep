package com.arman.dev.physioexamprep.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity

data class ChapterWithQuestions(
    @Embedded
    val chapter: ChapterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val questions: List<QuestionEntity>
) {
    /**
     * Room can neither filter nor order @Relation results, so use this for display:
     * active questions only, in stable ID order.
     */
    val activeQuestions: List<QuestionEntity>
        get() = questions.filter { it.isActive }.sortedBy { it.id }
}
