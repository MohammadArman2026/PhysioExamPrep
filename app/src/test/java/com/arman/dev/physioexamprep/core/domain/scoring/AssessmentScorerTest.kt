package com.arman.dev.physioexamprep.core.domain.scoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentScorerTest {

    private fun item(id: String, selected: Set<Long>, correct: Set<Long>, marks: Double = 4.0, negative: Double = 1.0) =
        ScoringItem(id, marks, negative, selected, correct)

    @Test
    fun singleCorrect() {
        assertTrue(AssessmentScorer.isCorrect(setOf(2), setOf(2)))
        assertFalse(AssessmentScorer.isCorrect(setOf(1), setOf(2)))
    }

    @Test
    fun multipleCorrect_isAllOrNothing() {
        assertTrue(AssessmentScorer.isCorrect(setOf(1, 2), setOf(2, 1)))
        assertFalse(AssessmentScorer.isCorrect(setOf(1), setOf(1, 2)))        // partial
        assertFalse(AssessmentScorer.isCorrect(setOf(1, 2, 3), setOf(1, 2)))  // extra
    }

    @Test
    fun emptySelection_isNeverCorrect_evenIfContentHasNoCorrectOption() {
        assertFalse(AssessmentScorer.isCorrect(emptySet(), emptySet()))
    }

    @Test
    fun score_withNegativeMarking() {
        val summary = AssessmentScorer.score(
            listOf(
                item("a", setOf(1), setOf(1)),        // +4
                item("b", setOf(5), setOf(6)),        // −1
                item("c", setOf(7, 8), setOf(7, 8)),  // +4
                item("d", emptySet(), setOf(9))       // 0
            )
        )
        assertEquals(4, summary.totalQuestions)
        assertEquals(3, summary.attemptedQuestions)
        assertEquals(2, summary.correctAnswers)
        assertEquals(1, summary.incorrectAnswers)
        assertEquals(1, summary.unattemptedQuestions)
        assertEquals(16.0, summary.totalMarks, 0.0)
        assertEquals(7.0, summary.obtainedMarks, 0.0)
        assertEquals(200.0 / 3, summary.accuracy, 1e-9)
        assertEquals(mapOf("a" to true, "b" to false, "c" to true), summary.outcomes)
    }

    @Test
    fun score_nothingAttempted() {
        val summary = AssessmentScorer.score(listOf(item("a", emptySet(), setOf(1))))
        assertEquals(0, summary.attemptedQuestions)
        assertEquals(0.0, summary.accuracy, 0.0)
        assertEquals(0.0, summary.obtainedMarks, 0.0)
    }
}
