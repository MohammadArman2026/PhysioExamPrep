package com.arman.dev.physioexamprep.core.domain.model

data class MockDefinition(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val mockType: MockType,
    val sourcePaperId: Long?,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val filterConfig: String?,
    val createdAt: Long,
    val updatedAt: Long,
    /** In position order. Empty when only the summary was loaded. */
    val questions: List<QuestionSelection> = emptyList()
)

/** A question chosen for a mock or session, with its scoring. */
data class QuestionSelection(
    val questionId: Long,
    val marks: Double = 1.0,
    val negativeMarks: Double = 0.0
)
