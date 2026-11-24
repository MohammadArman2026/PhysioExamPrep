package com.arman.dev.physioexamprep.feature.home.presentation

import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.home.domain.HomeDashboard

data class HomeState(
    val isLoading: Boolean = true,
    val greeting: String = "Good morning",
    val dashboard: HomeDashboard? = null,
) : UiState {
    val initials: String
        get() = dashboard?.userName.orEmpty().split(" ").filter { it.isNotBlank() }
            .take(2).joinToString("") { it.first().uppercase() }
}

sealed interface HomeIntent : UiIntent {
    data object ContinuePracticeClicked : HomeIntent
    data object ResumeClicked : HomeIntent
    data object QuickMixClicked : HomeIntent
    data object PyqOfTheDayClicked : HomeIntent
    data object SeeAllFocusAreasClicked : HomeIntent
    data class FocusAreaClicked(val subjectId: Long) : HomeIntent
    data object RegisterMockClicked : HomeIntent
    data object AvatarClicked : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class OpenQuiz(val request: QuizRequest) : HomeEffect
    data class OpenTopicPicker(val subjectId: Long) : HomeEffect
    data object OpenLearn : HomeEffect
    data object OpenProfile : HomeEffect
    data class ShowMessage(val text: String) : HomeEffect
}
