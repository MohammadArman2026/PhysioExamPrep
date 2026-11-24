package com.arman.dev.physioexamprep.feature.learn.presentation

import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.learn.domain.LearnOverview
import com.arman.dev.physioexamprep.feature.learn.domain.SubjectFilter
import com.arman.dev.physioexamprep.feature.learn.domain.SubjectSummary

data class LearnState(
    val isLoading: Boolean = true,
    val overview: LearnOverview? = null,
    val filter: SubjectFilter = SubjectFilter.ALL,
) : UiState {
    val visibleSubjects: List<SubjectSummary>
        get() = overview?.subjects.orEmpty().filter(filter::matches)
}

sealed interface LearnIntent : UiIntent {
    data class FilterSelected(val filter: SubjectFilter) : LearnIntent
    data class SubjectClicked(val subjectId: Long) : LearnIntent
    data object SearchClicked : LearnIntent
}

sealed interface LearnEffect : UiEffect {
    data class OpenTopicPicker(val subjectId: Long) : LearnEffect
    data class ShowMessage(val text: String) : LearnEffect
}
