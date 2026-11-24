package com.arman.dev.physioexamprep.feature.topicpicker.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.topicpicker.domain.TopicPickerRepository
import com.arman.dev.physioexamprep.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicPickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TopicPickerRepository,
) : MviViewModel<TopicPickerState, TopicPickerIntent, TopicPickerEffect>(TopicPickerState()) {

    private val subjectId: Long = checkNotNull(savedStateHandle.get<Long>(Routes.Topics.ARG_SUBJECT_ID))

    init {
        viewModelScope.launch {
            repository.observeSubject(subjectId).collect { subject ->
                setState {
                    // First load only: tick the suggested topics; later emissions keep the user's choice.
                    val preselect = if (this.subject == null && subject != null) subject.suggestedTopicIds else selectedTopicIds
                    copy(isLoading = false, subject = subject, selectedTopicIds = preselect)
                }
            }
        }
    }

    override fun handleIntent(intent: TopicPickerIntent) {
        when (intent) {
            TopicPickerIntent.BackClicked -> sendEffect(TopicPickerEffect.NavigateBack)
            TopicPickerIntent.BookmarkClicked -> currentState.subject?.let { s ->
                viewModelScope.launch { repository.setBookmarked(s.id, !s.isBookmarked) }
            }
            is TopicPickerIntent.TopicToggled -> setState {
                copy(selectedTopicIds = if (intent.topicId in selectedTopicIds) selectedTopicIds - intent.topicId else selectedTopicIds + intent.topicId)
            }
            TopicPickerIntent.SelectAllClicked -> setState {
                copy(selectedTopicIds = if (allSelected) emptySet() else subject?.topics.orEmpty().map { it.id }.toSet())
            }
            TopicPickerIntent.DecreaseQuestions -> setState {
                copy(questionsPerSession = (questionsPerSession - TopicPickerDefaults.STEP).coerceAtLeast(TopicPickerDefaults.MIN_QUESTIONS))
            }
            TopicPickerIntent.IncreaseQuestions -> setState {
                copy(questionsPerSession = (questionsPerSession + TopicPickerDefaults.STEP).coerceAtMost(TopicPickerDefaults.MAX_QUESTIONS))
            }
            is TopicPickerIntent.ModeSelected -> setState { copy(mode = intent.mode) }
            TopicPickerIntent.StartClicked -> {
                val s = currentState
                if (!s.canStart) return
                sendEffect(
                    TopicPickerEffect.OpenQuiz(
                        QuizRequest(
                            source = QuizSource.PRACTICE,
                            questionCount = s.questionsPerSession.coerceAtMost(s.selectedQuestionCount),
                            mode = s.mode,
                            subjectId = subjectId,
                            topicIds = s.selectedTopicIds.toList(),
                        )
                    )
                )
            }
        }
    }
}
