package com.arman.dev.physioexamprep.core.domain.usecase

import com.arman.dev.physioexamprep.core.domain.model.SessionResult
import com.arman.dev.physioexamprep.core.domain.repository.AssessmentRepository
import javax.inject.Inject

/** Submits a session: scores it and stores the result snapshot atomically. */
class CompleteAssessmentUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    suspend operator fun invoke(sessionId: String): SessionResult =
        assessmentRepository.completeSession(sessionId)
}
