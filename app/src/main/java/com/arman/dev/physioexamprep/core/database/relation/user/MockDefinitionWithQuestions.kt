package com.arman.dev.physioexamprep.core.database.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionQuestionEntity

data class MockDefinitionWithQuestions(
    @Embedded
    val mockDefinition: MockDefinitionEntity,
    @Relation(parentColumn = "id", entityColumn = "mockDefinitionId")
    val questions: List<MockDefinitionQuestionEntity>
) {
    /** Room does not order @Relation results. */
    val sortedQuestions: List<MockDefinitionQuestionEntity>
        get() = questions.sortedBy { it.position }
}
