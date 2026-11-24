package com.arman.dev.physioexamprep.feature.mockresult.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResultRepository
import com.arman.dev.physioexamprep.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MockResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MockResultRepository,
) : MviViewModel<MockResultState, MockResultIntent, MockResultEffect>(MockResultState()) {

    private val resultId: String = checkNotNull(savedStateHandle.get<String>(Routes.MockResult.ARG_RESULT_ID))

    init {
        viewModelScope.launch {
            repository.observeResult(resultId).collect { result ->
                setState { copy(isLoading = false, result = result) }
            }
        }
    }

    override fun handleIntent(intent: MockResultIntent) {
        val result = currentState.result
        when (intent) {
            MockResultIntent.CloseClicked -> sendEffect(MockResultEffect.NavigateBack)
            MockResultIntent.ShareClicked -> result?.let {
                val rank = it.ranking?.let { r -> " · Top ${r.topPercent}% (rank ${r.rank})" }.orEmpty()
                sendEffect(MockResultEffect.Share("I scored ${it.score}/${it.maxScore} on ${it.title} in PhysioPrep$rank"))
            }
            MockResultIntent.ReviewAnswersClicked ->
                sendEffect(MockResultEffect.ShowMessage("Answer review is coming soon"))
            MockResultIntent.PracticeWeakTopicsClicked -> result?.weakest?.let {
                sendEffect(MockResultEffect.OpenTopicPicker(it.subjectId))
            }
        }
    }
}
