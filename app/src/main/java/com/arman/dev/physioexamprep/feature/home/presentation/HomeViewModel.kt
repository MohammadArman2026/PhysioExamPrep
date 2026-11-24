package com.arman.dev.physioexamprep.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.home.domain.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        setState { copy(greeting = greetingFor(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))) }
        viewModelScope.launch {
            repository.observeDashboard().collect { dashboard ->
                setState { copy(isLoading = false, dashboard = dashboard) }
            }
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        val dashboard = currentState.dashboard ?: return
        when (intent) {
            HomeIntent.ContinuePracticeClicked -> sendEffect(
                HomeEffect.OpenQuiz(
                    QuizRequest(
                        source = QuizSource.PRACTICE,
                        questionCount = dashboard.dailyGoal.remaining.coerceAtLeast(1),
                    )
                )
            )
            HomeIntent.ResumeClicked -> dashboard.resume?.let {
                sendEffect(
                    HomeEffect.OpenQuiz(
                        QuizRequest(
                            source = QuizSource.RESUME,
                            questionCount = it.questionsLeft,
                            subjectId = it.subjectId,
                            topicIds = listOf(it.topicId),
                            refId = it.id,
                        )
                    )
                )
            }
            HomeIntent.QuickMixClicked -> sendEffect(
                HomeEffect.OpenQuiz(QuizRequest(source = QuizSource.QUICK_MIX, questionCount = dashboard.quickMix.questionCount))
            )
            HomeIntent.PyqOfTheDayClicked -> dashboard.pyqOfTheDay?.let {
                sendEffect(HomeEffect.OpenQuiz(QuizRequest(source = QuizSource.PYQ, questionCount = 1, refId = it.paperId)))
            }
            HomeIntent.SeeAllFocusAreasClicked -> sendEffect(HomeEffect.OpenLearn)
            is HomeIntent.FocusAreaClicked -> sendEffect(HomeEffect.OpenTopicPicker(intent.subjectId))
            HomeIntent.RegisterMockClicked -> dashboard.upcomingMock?.let { mock ->
                if (mock.isRegistered) return@let
                viewModelScope.launch {
                    repository.registerForMock(mock.id)
                    sendEffect(HomeEffect.ShowMessage("You're registered for ${mock.title}"))
                }
            }
            HomeIntent.AvatarClicked -> sendEffect(HomeEffect.OpenProfile)
        }
    }

    private fun greetingFor(hour: Int) = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
