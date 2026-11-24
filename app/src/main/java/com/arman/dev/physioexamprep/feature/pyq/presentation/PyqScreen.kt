package com.arman.dev.physioexamprep.feature.pyq.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.components.HeaderIconButton
import com.arman.dev.physioexamprep.core.ui.components.ScreenHeader
import com.arman.dev.physioexamprep.core.ui.components.SegmentedControl
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.pyq.data.FakePyqRepository
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqGrouping
import com.arman.dev.physioexamprep.feature.pyq.presentation.components.ExamCard
import com.arman.dev.physioexamprep.feature.pyq.presentation.components.PyqListRow
import com.arman.dev.physioexamprep.feature.pyq.presentation.components.PyqSearchField
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun PyqRoute(
    onOpenQuiz: (QuizRequest) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: PyqViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is PyqEffect.OpenQuiz -> onOpenQuiz(effect.request)
            is PyqEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
        }
    }
    PyqScreen(state, viewModel::onIntent, contentPadding)
}

/** Stateless UI for "06 · Past papers". */
@Composable
fun PyqScreen(state: PyqState, onIntent: (PyqIntent) -> Unit, contentPadding: PaddingValues = PaddingValues()) {
    if (state.isLoading) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Ink) }
        return
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ScreenHeader(
                title = "Past papers",
                subtitle = "Previous year questions · ${state.exams.size} exams",
                trailing = { HeaderIconButton(R.drawable.ic_sliders, "Filters", onClick = { onIntent(PyqIntent.FiltersClicked) }) },
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        item {
            PyqSearchField(state.query, { onIntent(PyqIntent.QueryChanged(it)) }, Modifier.padding(horizontal = 20.dp))
        }
        item {
            SegmentedControl(
                options = PyqGrouping.entries.map { it.label },
                selectedIndex = state.grouping.ordinal,
                onSelect = { onIntent(PyqIntent.GroupingSelected(PyqGrouping.entries[it])) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        when (state.grouping) {
            PyqGrouping.BY_EXAM -> items(state.visibleExams, key = { it.examId }) { exam ->
                ExamCard(
                    exam = exam,
                    onOpen = { onIntent(PyqIntent.ExamClicked(exam.examId)) },
                    onPaper = { onIntent(PyqIntent.PaperClicked(it)) },
                    onMore = { onIntent(PyqIntent.MoreYearsClicked(exam.examId)) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            PyqGrouping.BY_TOPIC -> items(state.visibleTopics, key = { it.subjectId }) { topic ->
                PyqListRow(
                    title = topic.subjectName,
                    subtitle = "${topic.questionCount} previous‑year questions",
                    onClick = { onIntent(PyqIntent.TopicClicked(topic.subjectId)) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            PyqGrouping.BY_YEAR -> items(state.years, key = { it.year }) { y ->
                PyqListRow(
                    title = "${y.year}",
                    subtitle = "${y.paperCount} papers · ${y.solvedCount} solved",
                    onClick = { onIntent(PyqIntent.YearClicked(y.year)) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }
        val empty = when (state.grouping) {
            PyqGrouping.BY_EXAM -> state.visibleExams.isEmpty()
            PyqGrouping.BY_TOPIC -> state.visibleTopics.isEmpty()
            PyqGrouping.BY_YEAR -> state.years.isEmpty()
        }
        if (empty) item {
            Text(
                "No papers match “${state.query}”",
                style = PulseType.BodySmall.copy(color = TextSecondary),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1064)
@Composable
private fun PyqPreview() {
    val repo = FakePyqRepository()
    val exams = runBlocking { repo.observeExams().first() }
    PhysioExamPrepTheme { PyqScreen(PyqState(isLoading = false, exams = exams), onIntent = {}) }
}
