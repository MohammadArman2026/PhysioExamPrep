package com.arman.dev.physioexamprep.feature.mockresult.presentation

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.components.IconBadge
import com.arman.dev.physioexamprep.core.ui.components.PulseButton
import com.arman.dev.physioexamprep.core.ui.components.PulseButtonStyle
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Ink3
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.core.ui.theme.White
import com.arman.dev.physioexamprep.core.data.fake.InMemorySessionResultStore
import com.arman.dev.physioexamprep.feature.mockresult.data.FakeMockResultRepository
import com.arman.dev.physioexamprep.feature.mockresult.presentation.components.InsightTiles
import com.arman.dev.physioexamprep.feature.mockresult.presentation.components.ScoreHeroContent
import com.arman.dev.physioexamprep.feature.mockresult.presentation.components.SubjectBreakdownCard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun MockResultRoute(
    onBack: () -> Unit,
    onOpenTopicPicker: (Long) -> Unit,
    viewModel: MockResultViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            MockResultEffect.NavigateBack -> onBack()
            is MockResultEffect.OpenTopicPicker -> onOpenTopicPicker(effect.subjectId)
            is MockResultEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
            is MockResultEffect.Share -> context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, effect.text),
                    null,
                )
            )
        }
    }
    BackHandler { viewModel.onIntent(MockResultIntent.CloseClicked) }
    MockResultScreen(state, viewModel::onIntent)
}

/** Stateless UI for "08 · Mock result". */
@Composable
fun MockResultScreen(state: MockResultState, onIntent: (MockResultIntent) -> Unit) {
    val result = state.result
    if (result == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) {
            if (state.isLoading) CircularProgressIndicator(color = Ink)
            else Text("Result not found", style = PulseType.Body.copy(color = TextSecondary))
        }
        return
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Ink)
                .statusBarsPadding()
                .padding(bottom = 28.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBadge(R.drawable.ic_x, size = 44.dp, iconSize = 20.dp, container = Ink3, tint = White, contentDescription = "Close", onClick = { onIntent(MockResultIntent.CloseClicked) })
                Text(
                    "${result.title} · Result",
                    style = PulseType.ListTitle.copy(color = White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                )
                IconBadge(R.drawable.ic_share, size = 44.dp, iconSize = 18.dp, container = Ink3, tint = White, contentDescription = "Share result", onClick = { onIntent(MockResultIntent.ShareClicked) })
            }
            ScoreHeroContent(result)
        }
        Column(
            Modifier
                .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 40.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SubjectBreakdownCard(result.subjects, showDelta = result.ranking != null)
            InsightTiles(result)
            PulseButton("Review answers", { onIntent(MockResultIntent.ReviewAnswersClicked) }, Modifier.fillMaxWidth(), style = PulseButtonStyle.Ink)
            if (result.weakest != null) {
                PulseButton(
                    "Practice weak topics", { onIntent(MockResultIntent.PracticeWeakTopicsClicked) }, Modifier.fillMaxWidth(),
                    style = PulseButtonStyle.Outline, showIcon = false,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1065)
@Composable
private fun MockResultPreview() {
    val result = runBlocking { FakeMockResultRepository(InMemorySessionResultStore()).observeResult("mock-2").first() }
    PhysioExamPrepTheme { MockResultScreen(MockResultState(false, result), onIntent = {}) }
}
