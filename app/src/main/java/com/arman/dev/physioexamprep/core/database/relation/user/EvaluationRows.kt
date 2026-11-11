package com.arman.dev.physioexamprep.core.database.relation.user

/**
 * Read-only rows used to score answers. They join user tables with content tables
 * (questions / question_options) in a single query, so evaluation never loads
 * question text and can run inside a write transaction.
 */
data class SessionQuestionEvaluationRow(
    val sessionQuestionId: String,
    val questionId: Long,
    val chapterId: Long,
    val questionType: String,
    val marks: Double,
    val negativeMarks: Double
)

data class SessionCorrectOptionRow(
    val sessionQuestionId: String,
    val optionId: Long
)
