package com.arman.dev.physioexamprep.feature.mocks.presentation

import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.mocks.domain.MockStatus
import com.arman.dev.physioexamprep.feature.mocks.domain.MocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MocksViewModel @Inject constructor(
    private val repository: MocksRepository,
) : MviViewModel<MocksState, MocksIntent, MocksEffect>(MocksState()) {

    init {
        viewModelScope.launch {
            repository.observeOverview().collect { overview ->
                setState { copy(isLoading = false, overview = overview) }
                updateCountdown()
            }
        }
        viewModelScope.launch {
            while (isActive) {
                updateCountdown()
                delay(1_000)
            }
        }
    }

    private fun updateCountdown() {
        val live = currentState.overview?.liveMock ?: return
        setState { copy(countdown = Countdown.until(live.startsAtMillis, System.currentTimeMillis())) }
    }

    override fun handleIntent(intent: MocksIntent) {
        val overview = currentState.overview ?: return
        when (intent) {
            MocksIntent.ReserveSeatClicked -> overview.liveMock?.let { live ->
                if (live.isReserved) return@let
                viewModelScope.launch {
                    repository.reserveSeat(live.id)
                    sendEffect(MocksEffect.ShowMessage("Seat reserved — we'll remind you before it starts"))
                }
            }
            is MocksIntent.MockClicked -> {
                val mock = overview.fullLength.firstOrNull { it.id == intent.mockId } ?: return
                when (val status = mock.status) {
                    is MockStatus.Completed -> sendEffect(MocksEffect.OpenResult(status.resultId))
                    MockStatus.Available -> sendEffect(
                        MocksEffect.OpenQuiz(
                            QuizRequest(QuizSource.MOCK, questionCount = mock.questionCount, mode = PracticeMode.EXAM, refId = mock.id)
                        )
                    )
                    MockStatus.Locked -> sendEffect(MocksEffect.ShowMessage("Finish the previous mock to unlock ${mock.title}"))
                }
            }
            MocksIntent.CustomMockClicked -> sendEffect(MocksEffect.ShowMessage("Custom mock builder is coming soon"))
            MocksIntent.CalendarClicked -> sendEffect(MocksEffect.ShowMessage("Mock calendar is coming soon"))
        }
    }
}
