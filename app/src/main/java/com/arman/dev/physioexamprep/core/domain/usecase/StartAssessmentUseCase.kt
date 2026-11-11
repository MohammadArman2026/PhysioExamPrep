package com.arman.dev.physioexamprep.core.domain.usecase

import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import com.arman.dev.physioexamprep.core.domain.repository.AssessmentRepository
import com.arman.dev.physioexamprep.core.domain.repository.MockDefinitionRepository
import javax.inject.Inject

/** Starts a session with a fixed question list. Returns the session id. */
class StartAssessmentUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    private val mockDefinitionRepository: MockDefinitionRepository
) {
    /** Practice / timed practice / PYQ practice over an explicit question list. */
    suspend operator fun invoke(
        userId: String,
        sessionType: SessionType,
        title: String,
        questions: List<QuestionSelection>,
        durationSeconds: Long? = null,
        sourcePaperId: Long? = null
    ): String {
        require(questions.isNotEmpty()) { "A session needs at least one question" }
        require(questions.map { it.questionId }.toSet().size == questions.size) { "Duplicate questions in session" }
        require(durationSeconds == null || durationSeconds > 0) { "Duration must be positive" }
        return assessmentRepository.startSession(
            userId = userId,
            sessionType = sessionType,
            title = title,
            questions = questions,
            durationSeconds = durationSeconds,
            sourcePaperId = sourcePaperId
        )
    }

    /** Mock attempt from a saved mock definition (questions and marks are copied). */
    suspend fun fromMockDefinition(userId: String, mockDefinitionId: String): String {
        val mock = requireNotNull(mockDefinitionRepository.getMockDefinition(mockDefinitionId)) {
            "Mock definition $mockDefinitionId not found"
        }
        require(mock.userId == userId) { "Mock definition belongs to another user" }
        require(mock.questions.isNotEmpty()) { "Mock definition has no questions" }
        return assessmentRepository.startSession(
            userId = userId,
            sessionType = SessionType.MOCK,
            title = mock.title,
            questions = mock.questions,
            durationSeconds = mock.durationMinutes * 60L,
            mockDefinitionId = mock.id,
            sourcePaperId = mock.sourcePaperId
        )
    }
}
