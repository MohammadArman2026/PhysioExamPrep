package com.arman.dev.physioexamprep.feature.quiz.presentation

import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.core.ui.components.OptionState
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizQuestion
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizSession

data class QuizState(
    val isLoading: Boolean = true,
    val session: QuizSession? = null,
    val index: Int = 0,
    /** questionId → chosen optionId. */
    val selections: Map<Long, Long> = emptyMap(),
    /** Questions whose answer has been checked (feedback shown, answer locked). */
    val checked: Set<Long> = emptySet(),
    val bookmarked: Set<Long> = emptySet(),
    val flagged: Set<Long> = emptySet(),
    val elapsedSeconds: Long = 0,
    val isSubmitting: Boolean = false,
    val showExitDialog: Boolean = false,
) : UiState {
    val current: QuizQuestion? get() = session?.questions?.getOrNull(index)
    val total: Int get() = session?.questions?.size ?: 0
    val isLast: Boolean get() = index == total - 1
    val selectedOptionId: Long? get() = current?.let { selections[it.id] }
    val isChecked: Boolean get() = current?.let { it.id in checked } ?: false
    val instantFeedback: Boolean get() = session?.mode?.showsInstantFeedback ?: true
    val isCorrect: Boolean get() = current?.let { selections[it.id] == it.correctOptionId } ?: false

    /** Chip in the quiz bar: counts down when timed, up when untimed. */
    val timerLabel: String
        get() {
            val limit = session?.timeLimitSeconds
            val secs = if (limit != null) (limit - elapsedSeconds).coerceAtLeast(0) else elapsedSeconds
            return "%02d:%02d".format(secs / 60, secs % 60)
        }

    fun optionState(optionId: Long): OptionState {
        val q = current ?: return OptionState.Default
        val selected = selections[q.id] == optionId
        return when {
            !isChecked -> if (selected) OptionState.Selected else OptionState.Default
            optionId == q.correctOptionId -> OptionState.Correct
            selected -> OptionState.Wrong
            else -> OptionState.Default
        }
    }

    /** Segment colours for the progress strip: done / current / upcoming. */
    fun segment(i: Int): Segment = when {
        i < index -> Segment.Done
        i == index -> Segment.Current
        else -> Segment.Upcoming
    }

    enum class Segment { Done, Current, Upcoming }
}

sealed interface QuizIntent : UiIntent {
    data class OptionSelected(val optionId: Long) : QuizIntent
    data object CheckAnswer : QuizIntent
    data object Next : QuizIntent
    data object ToggleBookmark : QuizIntent
    data object ToggleFlag : QuizIntent
    data object ExplainMore : QuizIntent
    data object CloseClicked : QuizIntent
    data object ConfirmExit : QuizIntent
    data object DismissExit : QuizIntent
}

sealed interface QuizEffect : UiEffect {
    data object NavigateBack : QuizEffect
    data class OpenResult(val resultId: String) : QuizEffect
    data class ShowMessage(val text: String) : QuizEffect
}
