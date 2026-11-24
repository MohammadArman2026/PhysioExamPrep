package com.arman.dev.physioexamprep.feature.mockresult.presentation

import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResult

data class MockResultState(
    val isLoading: Boolean = true,
    val result: MockResult? = null,
) : UiState

sealed interface MockResultIntent : UiIntent {
    data object CloseClicked : MockResultIntent
    data object ShareClicked : MockResultIntent
    data object ReviewAnswersClicked : MockResultIntent
    data object PracticeWeakTopicsClicked : MockResultIntent
}

sealed interface MockResultEffect : UiEffect {
    data object NavigateBack : MockResultEffect
    data class Share(val text: String) : MockResultEffect
    data class OpenTopicPicker(val subjectId: Long) : MockResultEffect
    data class ShowMessage(val text: String) : MockResultEffect
}
