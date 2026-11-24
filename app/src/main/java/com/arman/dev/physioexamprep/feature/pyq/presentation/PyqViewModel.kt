package com.arman.dev.physioexamprep.feature.pyq.presentation

import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqGrouping
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PyqViewModel @Inject constructor(
    repository: PyqRepository,
) : MviViewModel<PyqState, PyqIntent, PyqEffect>(PyqState()) {

    init {
        viewModelScope.launch {
            combine(repository.observeExams(), repository.observeTopicCounts()) { exams, topics -> exams to topics }
                .collect { (exams, topics) -> setState { copy(isLoading = false, exams = exams, topicCounts = topics) } }
        }
    }

    override fun handleIntent(intent: PyqIntent) {
        when (intent) {
            is PyqIntent.QueryChanged -> setState { copy(query = intent.query) }
            is PyqIntent.GroupingSelected -> setState { copy(grouping = intent.grouping) }
            is PyqIntent.ExamClicked -> {
                // Open the newest paper not solved yet (or the newest one if all are solved).
                val exam = currentState.exams.firstOrNull { it.examId == intent.examId } ?: return
                val paper = exam.papers.firstOrNull { !it.isSolved } ?: exam.papers.firstOrNull() ?: return
                openPaper(paper.id, paper.questionCount)
            }
            is PyqIntent.PaperClicked -> {
                val paper = currentState.exams.flatMap { it.papers }.firstOrNull { it.id == intent.paperId } ?: return
                openPaper(paper.id, paper.questionCount)
            }
            is PyqIntent.MoreYearsClicked -> setState { copy(grouping = PyqGrouping.BY_YEAR) }
            is PyqIntent.TopicClicked -> sendEffect(
                PyqEffect.OpenQuiz(QuizRequest(QuizSource.PYQ, questionCount = 20, subjectId = intent.subjectId))
            )
            is PyqIntent.YearClicked -> sendEffect(
                PyqEffect.OpenQuiz(QuizRequest(QuizSource.PYQ, questionCount = 20, refId = intent.year.toString()))
            )
            PyqIntent.FiltersClicked -> sendEffect(PyqEffect.ShowMessage("Filters are coming soon"))
        }
    }

    private fun openPaper(paperId: String, questionCount: Int) = sendEffect(
        PyqEffect.OpenQuiz(QuizRequest(QuizSource.PYQ, questionCount = questionCount, mode = PracticeMode.EXAM, refId = paperId))
    )
}
