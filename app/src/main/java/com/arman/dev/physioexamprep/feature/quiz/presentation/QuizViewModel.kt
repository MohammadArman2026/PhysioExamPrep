package com.arman.dev.physioexamprep.feature.quiz.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizRepository
import com.arman.dev.physioexamprep.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: QuizRepository,
) : MviViewModel<QuizState, QuizIntent, QuizEffect>(QuizState()) {

    private val request = Routes.Quiz.requestFrom(savedStateHandle)
    private var ticker: Job? = null

    init {
        viewModelScope.launch {
            val session = repository.startSession(request)
            setState { copy(isLoading = false, session = session) }
            startTicker()
        }
    }

    override fun handleIntent(intent: QuizIntent) {
        val s = currentState
        val question = s.current
        when (intent) {
            is QuizIntent.OptionSelected -> {
                if (question == null || s.isChecked) return
                setState { copy(selections = selections + (question.id to intent.optionId)) }
            }
            QuizIntent.CheckAnswer -> {
                if (question == null || s.selectedOptionId == null) return
                setState { copy(checked = checked + question.id) }
            }
            QuizIntent.Next -> if (s.isLast) submit() else setState { copy(index = index + 1) }
            QuizIntent.ToggleBookmark -> question?.let { q ->
                val now = q.id !in s.bookmarked
                setState { copy(bookmarked = if (now) bookmarked + q.id else bookmarked - q.id) }
                viewModelScope.launch { repository.setBookmarked(q.id, now) }
                sendEffect(QuizEffect.ShowMessage(if (now) "Saved to bookmarks" else "Removed from bookmarks"))
            }
            QuizIntent.ToggleFlag -> question?.let { q ->
                val now = q.id !in s.flagged
                setState { copy(flagged = if (now) flagged + q.id else flagged - q.id) }
                viewModelScope.launch { repository.setFlagged(q.id, now) }
                sendEffect(QuizEffect.ShowMessage(if (now) "Flagged for review" else "Flag removed"))
            }
            QuizIntent.ExplainMore -> sendEffect(QuizEffect.ShowMessage("Detailed explanations are coming soon"))
            QuizIntent.CloseClicked ->
                if (s.selections.isEmpty()) sendEffect(QuizEffect.NavigateBack) else setState { copy(showExitDialog = true) }
            QuizIntent.ConfirmExit -> {
                setState { copy(showExitDialog = false) }
                ticker?.cancel()
                sendEffect(QuizEffect.NavigateBack)
            }
            QuizIntent.DismissExit -> setState { copy(showExitDialog = false) }
        }
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                setState { copy(elapsedSeconds = elapsedSeconds + 1) }
                val limit = currentState.session?.timeLimitSeconds
                if (limit != null && currentState.elapsedSeconds >= limit) {
                    sendEffect(QuizEffect.ShowMessage("Time's up"))
                    submit()
                    break
                }
            }
        }
    }

    private fun submit() {
        val s = currentState
        val session = s.session ?: return
        if (s.isSubmitting) return
        setState { copy(isSubmitting = true) }
        ticker?.cancel()
        viewModelScope.launch {
            val resultId = repository.submit(session, s.selections, s.elapsedSeconds)
            sendEffect(QuizEffect.OpenResult(resultId))
        }
    }
}
