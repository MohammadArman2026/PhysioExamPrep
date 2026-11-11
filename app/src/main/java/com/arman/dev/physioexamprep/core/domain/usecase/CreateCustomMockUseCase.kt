package com.arman.dev.physioexamprep.core.domain.usecase

import com.arman.dev.physioexamprep.core.domain.model.MockType
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.repository.MockDefinitionRepository
import javax.inject.Inject

/** Validates and saves a user-built mock with its selected questions. Returns its id. */
class CreateCustomMockUseCase @Inject constructor(
    private val mockDefinitionRepository: MockDefinitionRepository
) {
    suspend operator fun invoke(
        userId: String,
        title: String,
        description: String?,
        durationMinutes: Int,
        questions: List<QuestionSelection>,
        filterConfig: String? = null
    ): String {
        require(title.isNotBlank()) { "Mock title must not be blank" }
        require(durationMinutes > 0) { "Duration must be positive" }
        require(questions.isNotEmpty()) { "A mock needs at least one question" }
        require(questions.map { it.questionId }.toSet().size == questions.size) { "Duplicate questions in mock" }
        require(questions.all { it.marks > 0 && it.negativeMarks >= 0 }) { "Invalid marks" }
        return mockDefinitionRepository.createMockDefinition(
            userId = userId,
            title = title.trim(),
            description = description,
            mockType = MockType.CUSTOM,
            sourcePaperId = null,
            durationMinutes = durationMinutes,
            questions = questions,
            filterConfig = filterConfig
        )
    }
}
