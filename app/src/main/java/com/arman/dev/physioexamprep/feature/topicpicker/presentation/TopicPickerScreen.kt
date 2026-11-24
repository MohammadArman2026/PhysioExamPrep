package com.arman.dev.physioexamprep.feature.topicpicker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.subject.subjectVisual
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.CardSurface
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.topicpicker.data.FakeTopicPickerRepository
import com.arman.dev.physioexamprep.feature.topicpicker.presentation.components.PracticeActionSheet
import com.arman.dev.physioexamprep.feature.topicpicker.presentation.components.TopicRow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopicPickerRoute(
    onBack: () -> Unit,
    onOpenQuiz: (QuizRequest) -> Unit,
    viewModel: TopicPickerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            TopicPickerEffect.NavigateBack -> onBack()
            is TopicPickerEffect.OpenQuiz -> onOpenQuiz(effect.request)
        }
    }
    TopicPickerScreen(state, viewModel::onIntent)
}

/** Stateless UI for "03 · Topic picker". */
@Composable
fun TopicPickerScreen(state: TopicPickerState, onIntent: (TopicPickerIntent) -> Unit) {
    val subject = state.subject
    if (subject == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) {
            if (state.isLoading) CircularProgressIndicator(color = Ink)
            else Text("Subject not found", style = PulseType.Body.copy(color = TextSecondary))
        }
        return
    }
    val visual = subjectVisual(subject.id)
    val fmt = NumberFormat.getIntegerInstance(Locale.US)
    Column(Modifier.fillMaxSize().background(AppCanvas)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Subject header (soft subject colour, runs behind the status bar).
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(visual.soft)
                    .statusBarsPadding()
                    .padding(bottom = 52.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconBadge(R.drawable.ic_chev_left, size = 44.dp, iconSize = 20.dp, contentDescription = "Back", onClick = { onIntent(TopicPickerIntent.BackClicked) })
                    IconBadge(
                        R.drawable.ic_bookmark, size = 44.dp, iconSize = 18.dp,
                        container = if (subject.isBookmarked) Ink else CardSurface,
                        tint = if (subject.isBookmarked) CardSurface else Ink,
                        contentDescription = if (subject.isBookmarked) "Remove bookmark" else "Bookmark subject",
                        onClick = { onIntent(TopicPickerIntent.BookmarkClicked) },
                    )
                }
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("SUBJECT", style = PulseType.Overline.copy(color = visual.accent))
                    Text(subject.name, style = PulseType.SubjectTitle.copy(color = Ink))
                    Text(
                        "${subject.topics.size} topics · ${fmt.format(subject.questionCount)} questions",
                        style = PulseType.BodySmall.copy(color = TextSecondary),
                    )
                }
                Row(
                    Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatBox("${subject.completePercent}%", "Complete", Modifier.weight(1f))
                    StatBox("${subject.accuracyPercent}%", "Accuracy", Modifier.weight(1f))
                    StatBox(formatMinutes(subject.timeSpentMinutes), "Time spent", Modifier.weight(1f))
                }
            }
            // Topics sheet overlaps the header by 28dp.
            Column(
                Modifier
                    .offset(y = (-28).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(AppCanvas)
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Choose topics", style = PulseType.SheetTitle.copy(color = Ink))
                        Text(
                            "${state.selectedTopicIds.size} selected · ${fmt.format(state.selectedQuestionCount)} questions",
                            style = PulseType.Meta.copy(color = TextSecondary),
                        )
                    }
                    Text(
                        if (state.allSelected) "Clear all" else "Select all",
                        style = PulseType.Chip.copy(color = visual.accent, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(role = Role.Button) { onIntent(TopicPickerIntent.SelectAllClicked) },
                    )
                }
                Spacer(Modifier.height(4.dp))
                subject.topics.forEach { topic ->
                    TopicRow(
                        topic = topic,
                        selected = topic.id in state.selectedTopicIds,
                        onToggle = { onIntent(TopicPickerIntent.TopicToggled(topic.id)) },
                    )
                }
            }
        }
        PracticeActionSheet(
            questions = state.questionsPerSession,
            estimatedMinutes = state.estimatedMinutes,
            mode = state.mode,
            canDecrease = state.canDecrease,
            canIncrease = state.canIncrease,
            canStart = state.canStart,
            onIntent = onIntent,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(value, style = PulseType.SectionTitle.copy(color = Ink), maxLines = 1)
        Text(label, style = PulseType.Caption.copy(color = TextSecondary), maxLines = 1)
    }
}

private fun formatMinutes(total: Int): String = when {
    total < 60 -> "${total}m"
    total % 60 == 0 -> "${total / 60}h"
    else -> "${total / 60}h ${total % 60}m"
}

@Preview(showBackground = true, heightDp = 1180)
@Composable
private fun TopicPickerPreview() {
    val subject = runBlocking { FakeTopicPickerRepository().observeSubject(1L).first() }
    PhysioExamPrepTheme {
        TopicPickerScreen(
            TopicPickerState(isLoading = false, subject = subject, selectedTopicIds = subject!!.suggestedTopicIds),
            onIntent = {},
        )
    }
}
