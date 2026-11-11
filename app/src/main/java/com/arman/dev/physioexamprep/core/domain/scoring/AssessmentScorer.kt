package com.arman.dev.physioexamprep.core.domain.scoring

/** Scoring input for one session question. */
data class ScoringItem(
    val sessionQuestionId: String,
    val marks: Double,
    val negativeMarks: Double,
    val selectedOptionIds: Set<Long>,
    val correctOptionIds: Set<Long>
)

data class ScoreSummary(
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unattemptedQuestions: Int,
    val totalMarks: Double,
    val obtainedMarks: Double,
    /** Percentage (0–100) of attempted questions answered correctly. */
    val accuracy: Double,
    /** sessionQuestionId -> correct? (attempted questions only) */
    val outcomes: Map<String, Boolean>
)

/**
 * Pure scoring rules:
 *  - An answer is correct only if the selected set equals the set of correct options
 *    (all-or-nothing, also for multiple-correct questions).
 *  - Correct: +marks. Incorrect: −negativeMarks. Unattempted: 0.
 */
object AssessmentScorer {

    fun isCorrect(selectedOptionIds: Set<Long>, correctOptionIds: Set<Long>): Boolean =
        selectedOptionIds.isNotEmpty() && selectedOptionIds == correctOptionIds

    fun score(items: List<ScoringItem>): ScoreSummary {
        val outcomes = LinkedHashMap<String, Boolean>()
        var obtained = 0.0
        for (item in items) {
            if (item.selectedOptionIds.isEmpty()) continue
            val correct = isCorrect(item.selectedOptionIds, item.correctOptionIds)
            outcomes[item.sessionQuestionId] = correct
            obtained += if (correct) item.marks else -item.negativeMarks
        }
        val attempted = outcomes.size
        val correctCount = outcomes.values.count { it }
        return ScoreSummary(
            totalQuestions = items.size,
            attemptedQuestions = attempted,
            correctAnswers = correctCount,
            incorrectAnswers = attempted - correctCount,
            unattemptedQuestions = items.size - attempted,
            totalMarks = items.sumOf { it.marks },
            obtainedMarks = obtained,
            accuracy = if (attempted == 0) 0.0 else correctCount * 100.0 / attempted,
            outcomes = outcomes
        )
    }
}
