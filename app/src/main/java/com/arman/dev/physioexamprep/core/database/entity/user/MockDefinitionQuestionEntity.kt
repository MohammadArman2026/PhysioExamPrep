package com.arman.dev.physioexamprep.core.database.entity.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity

/**
 * A question selected for a mock definition.
 * The content FK uses RESTRICT: retire questions with QuestionEntity.isActive
 * instead of deleting them once users have built mocks with them.
 */
@Entity(
    tableName = "mock_definition_questions",
    foreignKeys = [
        ForeignKey(
            entity = MockDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["mockDefinitionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["mockDefinitionId", "position"], unique = true),
        Index(value = ["mockDefinitionId", "questionId"], unique = true),
        Index(value = ["questionId"])
    ]
)
data class MockDefinitionQuestionEntity(
    @PrimaryKey
    val id: String,
    val mockDefinitionId: String,
    val questionId: Long,
    val position: Int,
    val marks: Double,
    val negativeMarks: Double
)
