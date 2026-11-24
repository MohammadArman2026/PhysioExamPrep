package com.arman.dev.physioexamprep.feature.topicpicker.presentation

import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.topicpicker.domain.SubjectDetail

data class TopicPickerState(
    val isLoading: Boolean = true,
    val subject: SubjectDetail? = null,
    val selectedTopicIds: Set<Long> = emptySet(),
    val questionsPerSession: Int = TopicPickerDefaults.DEFAULT_QUESTIONS,
    val mode: PracticeMode = PracticeMode.PRACTICE,
) : UiState {
    val selectedQuestionCount: Int
        get() = subject?.topics.orEmpty().filter { it.id in selectedTopicIds }.sumOf { it.questionCount }
    val allSelected: Boolean
        get() = subject != null && subject.topics.isNotEmpty() && selectedTopicIds.size == subject.topics.size
    val canDecrease: Boolean get() = questionsPerSession > TopicPickerDefaults.MIN_QUESTIONS
    val canIncrease: Boolean get() = questionsPerSession < TopicPickerDefaults.MAX_QUESTIONS
    val canStart: Boolean get() = selectedTopicIds.isNotEmpty()
    /** ~0.8 min per question (Figma: 20 Qs → ~16 min). */
    val estimatedMinutes: Int get() = (questionsPerSession * 0.8f).toInt()
}

object TopicPickerDefaults {
    const val MIN_QUESTIONS = 5
    const val MAX_QUESTIONS = 100
    const val STEP = 5
    const val DEFAULT_QUESTIONS = 20
}

sealed interface TopicPickerIntent : UiIntent {
    data object BackClicked : TopicPickerIntent
    data object BookmarkClicked : TopicPickerIntent
    data class TopicToggled(val topicId: Long) : TopicPickerIntent
    data object SelectAllClicked : TopicPickerIntent
    data object DecreaseQuestions : TopicPickerIntent
    data object IncreaseQuestions : TopicPickerIntent
    data class ModeSelected(val mode: PracticeMode) : TopicPickerIntent
    data object StartClicked : TopicPickerIntent
}

sealed interface TopicPickerEffect : UiEffect {
    data object NavigateBack : TopicPickerEffect
    data class OpenQuiz(val request: QuizRequest) : TopicPickerEffect
}
