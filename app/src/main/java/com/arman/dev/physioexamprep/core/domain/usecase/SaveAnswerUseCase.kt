package com.arman.dev.physioexamprep.core.domain.usecase

import com.arman.dev.physioexamprep.core.domain.model.AnswerOutcome
import com.arman.dev.physioexamprep.core.domain.repository.AssessmentRepository
import javax.inject.Inject

/**
 * Saves the user's selection for one session question.
 * Returns immediate feedback for practice-style sessions, null for mocks.
 */
class SaveAnswerUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    suspend operator fun invoke(
        sessionQuestionId: String,
        selectedOptionIds: Set<Long>,
        additionalTimeSeconds: Long = 0
    ): AnswerOutcome? {
        require(additionalTimeSeconds >= 0) { "Time spent cannot be negative" }
        return assessmentRepository.saveAnswer(sessionQuestionId, selectedOptionIds, additionalTimeSeconds)
    }
}
