package com.arman.dev.physioexamprep.feature.quiz.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.components.OptionRow
import com.arman.dev.physioexamprep.core.ui.components.Pill
import com.arman.dev.physioexamprep.core.ui.subject.subjectVisual
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizSession
import com.arman.dev.physioexamprep.feature.quiz.data.FakeQuestionBank
import com.arman.dev.physioexamprep.feature.quiz.presentation.components.ExplanationSheet
import com.arman.dev.physioexamprep.feature.quiz.presentation.components.QuizActions
import com.arman.dev.physioexamprep.feature.quiz.presentation.components.QuizBar

@Composable
fun QuizRoute(
    onBack: () -> Unit,
    onOpenResult: (String) -> Unit,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            QuizEffect.NavigateBack -> onBack()
            is QuizEffect.OpenResult -> onOpenResult(effect.resultId)
            is QuizEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
        }
    }
    BackHandler { viewModel.onIntent(QuizIntent.CloseClicked) }
    QuizScreen(state, viewModel::onIntent)
}

/** Stateless UI for "04 · Quiz" and, once the answer is checked, "05 · Answer feedback". */
@Composable
fun QuizScreen(state: QuizState, onIntent: (QuizIntent) -> Unit) {
    val question = state.current
    if (question == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) {
            if (state.isLoading) CircularProgressIndicator(color = Ink)
            else Text("No questions available", style = PulseType.Body.copy(color = TextSecondary))
        }
        return
    }
    val visual = subjectVisual(question.subjectId)
    val scroll = rememberScrollState()
    val showFeedback = state.isChecked && state.instantFeedback
    LaunchedEffect(showFeedback, state.index) {
        if (showFeedback) scroll.animateScrollTo(scroll.maxValue) else scroll.scrollTo(0)
    }

    Column(Modifier.fillMaxSize().background(AppCanvas)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .statusBarsPadding(),
        ) {
            QuizBar(
                total = state.total,
                segment = state::segment,
                currentColor = visual.accent,
                timerLabel = state.timerLabel,
                onClose = { onIntent(QuizIntent.CloseClicked) },
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 26.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Question ${state.index + 1} of ${state.total}",
                    style = PulseType.Chip.copy(color = TextSecondary),
                    modifier = Modifier.weight(1f),
                )
                Pill(
                    "${question.subjectName} · ${question.topicName}", visual.soft, visual.accent,
                    horizontalPadding = 12.dp, verticalPadding = 6.dp,
                )
            }
            Text(
                question.text,
                style = PulseType.Question.copy(color = Ink),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp),
            )
            Column(
                Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                question.options.forEachIndexed { i, option ->
                    OptionRow(
                        letter = ('A' + i).toString(),
                        answer = option.text,
                        state = state.optionState(option.id),
                        enabled = !state.isChecked,
                        onClick = { onIntent(QuizIntent.OptionSelected(option.id)) },
                    )
                }
            }
            if (showFeedback) {
                Spacer(Modifier.height(24.dp))
                ExplanationSheet(
                    question = question,
                    selectedOptionId = state.selectedOptionId,
                    isCorrect = state.isCorrect,
                    isLast = state.isLast,
                    onExplainMore = { onIntent(QuizIntent.ExplainMore) },
                    onNext = { onIntent(QuizIntent.Next) },
                    modifier = Modifier.navigationBarsPadding(),
                )
            } else {
                Spacer(Modifier.height(24.dp))
            }
        }
        if (!showFeedback) {
            QuizActions(
                isBookmarked = question.id in state.bookmarked,
                isFlagged = question.id in state.flagged,
                primaryLabel = when {
                    state.instantFeedback -> "Check answer"
                    state.isLast -> "Submit"
                    else -> "Next"
                },
                primaryEnabled = !state.isSubmitting && (!state.instantFeedback || state.selectedOptionId != null),
                onFlag = { onIntent(QuizIntent.ToggleFlag) },
                onBookmark = { onIntent(QuizIntent.ToggleBookmark) },
                onPrimary = { onIntent(if (state.instantFeedback) QuizIntent.CheckAnswer else QuizIntent.Next) },
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    }

    if (state.showExitDialog) {
        AlertDialog(
            onDismissRequest = { onIntent(QuizIntent.DismissExit) },
            title = { Text("Leave this session?", style = PulseType.SheetTitle) },
            text = { Text("Your answers so far won't be scored.", style = PulseType.Body) },
            confirmButton = { TextButton(onClick = { onIntent(QuizIntent.ConfirmExit) }) { Text("Leave") } },
            dismissButton = { TextButton(onClick = { onIntent(QuizIntent.DismissExit) }) { Text("Keep going") } },
            containerColor = AppCanvas,
        )
    }
}

private fun previewState(checked: Boolean): QuizState {
    val qs = FakeQuestionBank.questions.take(20)
    val first = qs.first()
    return QuizState(
        isLoading = false,
        session = QuizSession("preview", "Anatomy", PracticeMode.PRACTICE, qs, null),
        index = 0,
        selections = mapOf(first.id to first.options[if (checked) 0 else 1].id),
        checked = if (checked) setOf(first.id) else emptySet(),
        elapsedSeconds = 522,
    )
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun QuizPreview() = PhysioExamPrepTheme { QuizScreen(previewState(checked = false), onIntent = {}) }

@Preview(showBackground = true, heightDp = 995)
@Composable
private fun AnswerFeedbackPreview() = PhysioExamPrepTheme { QuizScreen(previewState(checked = true), onIntent = {}) }
