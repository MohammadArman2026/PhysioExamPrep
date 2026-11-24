package com.arman.dev.physioexamprep.feature.learn.domain

import kotlinx.coroutines.flow.Flow

data class SubjectSummary(
    val id: Long,
    val name: String,
    val questionCount: Int,
    val topicCount: Int,
    /** 0..100 */
    val masteryPercent: Int,
    /** Shows "New" instead of a percentage (content just added). */
    val isNew: Boolean = false,
    val isSaved: Boolean = false,
) {
    val isStarted: Boolean get() = masteryPercent > 0
}

data class LearnOverview(
    val subjects: List<SubjectSummary>,
    /** 0..100 */
    val overallMasteryPercent: Int,
    /** Percentage points gained this week, e.g. +6. */
    val weeklyChangePercent: Int,
) {
    val totalQuestions: Int get() = subjects.sumOf { it.questionCount }
}

enum class SubjectFilter(val label: String) {
    ALL("All"), IN_PROGRESS("In progress"), NOT_STARTED("Not started"), SAVED("Saved");

    fun matches(subject: SubjectSummary): Boolean = when (this) {
        ALL -> true
        IN_PROGRESS -> subject.isStarted
        NOT_STARTED -> !subject.isStarted
        SAVED -> subject.isSaved
    }
}

interface LearnRepository {
    fun observeOverview(): Flow<LearnOverview>
}
