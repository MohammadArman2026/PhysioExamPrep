package com.arman.dev.physioexamprep.feature.mocks.presentation

import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.mocks.domain.MocksOverview

data class MocksState(
    val isLoading: Boolean = true,
    val overview: MocksOverview? = null,
    /** Re-computed every second by the ViewModel. */
    val countdown: Countdown = Countdown(0, 0, 0, 0),
) : UiState

data class Countdown(val days: Long, val hours: Long, val minutes: Long, val seconds: Long) {
    companion object {
        fun until(targetMillis: Long, nowMillis: Long): Countdown {
            val total = ((targetMillis - nowMillis) / 1000).coerceAtLeast(0)
            return Countdown(total / 86_400, total % 86_400 / 3_600, total % 3_600 / 60, total % 60)
        }
    }
}

sealed interface MocksIntent : UiIntent {
    data object ReserveSeatClicked : MocksIntent
    data class MockClicked(val mockId: String) : MocksIntent
    data object CustomMockClicked : MocksIntent
    data object CalendarClicked : MocksIntent
}

sealed interface MocksEffect : UiEffect {
    data class OpenQuiz(val request: QuizRequest) : MocksEffect
    data class OpenResult(val resultId: String) : MocksEffect
    data class ShowMessage(val text: String) : MocksEffect
}
