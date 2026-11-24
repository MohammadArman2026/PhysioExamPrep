package com.arman.dev.physioexamprep.feature.mocks.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
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
import com.arman.dev.physioexamprep.core.ui.components.SectionHeader
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.feature.mocks.data.FakeMocksRepository
import com.arman.dev.physioexamprep.feature.mocks.presentation.components.CustomMockCard
import com.arman.dev.physioexamprep.feature.mocks.presentation.components.LiveMockHero
import com.arman.dev.physioexamprep.feature.mocks.presentation.components.MockList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun MocksRoute(
    onOpenQuiz: (QuizRequest) -> Unit,
    onOpenResult: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: MocksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is MocksEffect.OpenQuiz -> onOpenQuiz(effect.request)
            is MocksEffect.OpenResult -> onOpenResult(effect.resultId)
            is MocksEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
        }
    }
    MocksScreen(state, viewModel::onIntent, contentPadding)
}

/** Stateless UI for "07 · Mock tests". */
@Composable
fun MocksScreen(state: MocksState, onIntent: (MocksIntent) -> Unit, contentPadding: PaddingValues = PaddingValues()) {
    val overview = state.overview
    if (overview == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Ink) }
        return
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        item {
            ScreenHeader(
                title = "Mock tests",
                subtitle = "Simulate the real exam, end to end",
                trailing = { HeaderIconButton(R.drawable.ic_calendar, "Mock calendar", onClick = { onIntent(MocksIntent.CalendarClicked) }) },
            )
        }
        overview.liveMock?.let { live ->
            item {
                LiveMockHero(
                    mock = live,
                    countdown = state.countdown,
                    onReserve = { onIntent(MocksIntent.ReserveSeatClicked) },
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp),
                )
            }
        }
        item {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Full‑length mocks", action = "${overview.fullLength.size} tests")
                MockList(overview.fullLength, onClick = { onIntent(MocksIntent.MockClicked(it)) })
            }
        }
        item {
            CustomMockCard(
                onClick = { onIntent(MocksIntent.CustomMockClicked) },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1040)
@Composable
private fun MocksPreview() {
    val overview = runBlocking { FakeMocksRepository().observeOverview().first() }
    PhysioExamPrepTheme { MocksScreen(MocksState(false, overview, Countdown(4, 12, 36, 8)), onIntent = {}) }
}
