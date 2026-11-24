package com.arman.dev.physioexamprep.feature.learn.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.components.HeaderIconButton
import com.arman.dev.physioexamprep.core.ui.components.PulseChip
import com.arman.dev.physioexamprep.core.ui.components.ScreenHeader
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.learn.data.FakeLearnRepository
import com.arman.dev.physioexamprep.feature.learn.domain.SubjectFilter
import com.arman.dev.physioexamprep.feature.learn.presentation.components.OverallMasteryCard
import com.arman.dev.physioexamprep.feature.learn.presentation.components.SubjectTile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LearnRoute(
    onOpenTopicPicker: (Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: LearnViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is LearnEffect.OpenTopicPicker -> onOpenTopicPicker(effect.subjectId)
            is LearnEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
        }
    }
    LearnScreen(state, viewModel::onIntent, contentPadding)
}

/** Stateless UI for "02 · Learn". */
@Composable
fun LearnScreen(
    state: LearnState,
    onIntent: (LearnIntent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val overview = state.overview
    if (overview == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Ink)
        }
        return
    }
    val total = NumberFormat.getIntegerInstance(Locale.US).format(overview.totalQuestions)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ScreenHeader(
                title = "Learn",
                subtitle = "${overview.subjects.size} subjects · $total questions",
                horizontalPadding = 0.dp,
                trailing = {
                    HeaderIconButton(R.drawable.ic_search, "Search", onClick = { onIntent(LearnIntent.SearchClicked) })
                },
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SubjectFilter.entries.forEach { filter ->
                    PulseChip(filter.label, selected = filter == state.filter, onClick = { onIntent(LearnIntent.FilterSelected(filter)) })
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            OverallMasteryCard(overview, Modifier.padding(top = 4.dp, bottom = 2.dp))
        }
        items(state.visibleSubjects, key = { it.id }) { subject ->
            SubjectTile(subject, onClick = { onIntent(LearnIntent.SubjectClicked(subject.id)) })
        }
        if (state.visibleSubjects.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Nothing here yet",
                    style = PulseType.BodySmall.copy(color = TextSecondary),
                    modifier = Modifier.padding(vertical = 32.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1054)
@Composable
private fun LearnScreenPreview() {
    val overview = runBlocking { FakeLearnRepository().observeOverview().first() }
    PhysioExamPrepTheme { LearnScreen(LearnState(isLoading = false, overview = overview), onIntent = {}) }
}
