package com.arman.dev.physioexamprep.feature.pyq.presentation

import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.pyq.domain.ExamPapers
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqGrouping
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqTopicCount

data class PyqState(
    val isLoading: Boolean = true,
    val exams: List<ExamPapers> = emptyList(),
    val topicCounts: List<PyqTopicCount> = emptyList(),
    val query: String = "",
    val grouping: PyqGrouping = PyqGrouping.BY_EXAM,
) : UiState {
    /** Matches exam name/description or a year. */
    val visibleExams: List<ExamPapers>
        get() {
            val q = query.trim()
            if (q.isEmpty()) return exams
            return exams.filter { e ->
                e.name.contains(q, true) || e.description.contains(q, true) || e.papers.any { it.year.toString().startsWith(q) }
            }
        }

    val visibleTopics: List<PyqTopicCount>
        get() = topicCounts.filter { query.isBlank() || it.subjectName.contains(query.trim(), true) }

    /** year → (papers, solved) across all visible exams, newest first. */
    val years: List<YearSummary>
        get() = visibleExams.flatMap { it.papers }.groupBy { it.year }
            .map { (year, papers) -> YearSummary(year, papers.size, papers.count { it.isSolved }) }
            .sortedByDescending { it.year }

    data class YearSummary(val year: Int, val paperCount: Int, val solvedCount: Int)
}

sealed interface PyqIntent : UiIntent {
    data class QueryChanged(val query: String) : PyqIntent
    data class GroupingSelected(val grouping: PyqGrouping) : PyqIntent
    data class ExamClicked(val examId: String) : PyqIntent
    data class PaperClicked(val paperId: String) : PyqIntent
    data class MoreYearsClicked(val examId: String) : PyqIntent
    data class TopicClicked(val subjectId: Long) : PyqIntent
    data class YearClicked(val year: Int) : PyqIntent
    data object FiltersClicked : PyqIntent
}

sealed interface PyqEffect : UiEffect {
    data class OpenQuiz(val request: QuizRequest) : PyqEffect
    data class ShowMessage(val text: String) : PyqEffect
}
