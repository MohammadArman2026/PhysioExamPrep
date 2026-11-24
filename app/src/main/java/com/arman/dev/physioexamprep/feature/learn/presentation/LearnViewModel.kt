package com.arman.dev.physioexamprep.feature.learn.presentation

import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.learn.domain.LearnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    repository: LearnRepository,
) : MviViewModel<LearnState, LearnIntent, LearnEffect>(LearnState()) {

    init {
        viewModelScope.launch {
            repository.observeOverview().collect { overview ->
                setState { copy(isLoading = false, overview = overview) }
            }
        }
    }

    override fun handleIntent(intent: LearnIntent) {
        when (intent) {
            is LearnIntent.FilterSelected -> setState { copy(filter = intent.filter) }
            is LearnIntent.SubjectClicked -> sendEffect(LearnEffect.OpenTopicPicker(intent.subjectId))
            LearnIntent.SearchClicked -> sendEffect(LearnEffect.ShowMessage("Search is coming soon"))
        }
    }
}
