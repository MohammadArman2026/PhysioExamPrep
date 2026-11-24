package com.arman.dev.physioexamprep.feature.home.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.mvi.CollectEffects
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Coral
import com.arman.dev.physioexamprep.core.ui.theme.CoralSoft
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.Lilac
import com.arman.dev.physioexamprep.core.ui.theme.LilacSoft
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.core.ui.theme.TextSecondary
import com.arman.dev.physioexamprep.feature.home.data.FakeHomeRepository
import com.arman.dev.physioexamprep.feature.home.presentation.components.BentoTiles
import com.arman.dev.physioexamprep.feature.home.presentation.components.DailyGoalHero
import com.arman.dev.physioexamprep.feature.home.presentation.components.FocusAreasCard
import com.arman.dev.physioexamprep.feature.home.presentation.components.ResumeCard
import com.arman.dev.physioexamprep.feature.home.presentation.components.UpcomingMockCard
import com.arman.dev.physioexamprep.core.ui.components.SectionHeader
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/** Stateful entry: owns the ViewModel and turns effects into navigation. */
@Composable
fun HomeRoute(
    onOpenQuiz: (QuizRequest) -> Unit,
    onOpenTopicPicker: (Long) -> Unit,
    onOpenLearn: () -> Unit,
    onOpenProfile: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is HomeEffect.OpenQuiz -> onOpenQuiz(effect.request)
            is HomeEffect.OpenTopicPicker -> onOpenTopicPicker(effect.subjectId)
            HomeEffect.OpenLearn -> onOpenLearn()
            HomeEffect.OpenProfile -> onOpenProfile()
            is HomeEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
        }
    }
    HomeScreen(state = state, onIntent = viewModel::onIntent, contentPadding = contentPadding)
}

/** Stateless UI for "01 · Home". */
@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val dashboard = state.dashboard
    if (dashboard == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Ink)
        }
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        item {
            HomeHeader(
                greeting = state.greeting,
                name = dashboard.userName,
                initials = state.initials,
                streakDays = dashboard.streakDays,
                onAvatarClick = { onIntent(HomeIntent.AvatarClicked) },
            )
        }
        item {
            DailyGoalHero(
                goal = dashboard.dailyGoal,
                streakDays = dashboard.streakDays,
                onContinue = { onIntent(HomeIntent.ContinuePracticeClicked) },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
            )
        }
        dashboard.resume?.let { resume ->
            item {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Pick up where you left off")
                    ResumeCard(resume, onClick = { onIntent(HomeIntent.ResumeClicked) })
                }
            }
        }
        item {
            BentoTiles(
                quickMix = dashboard.quickMix,
                pyq = dashboard.pyqOfTheDay,
                onQuickMix = { onIntent(HomeIntent.QuickMixClicked) },
                onPyq = { onIntent(HomeIntent.PyqOfTheDayClicked) },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 26.dp),
            )
        }
        if (dashboard.focusAreas.isNotEmpty()) {
            item {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Focus areas", action = "See all", onAction = { onIntent(HomeIntent.SeeAllFocusAreasClicked) })
                    FocusAreasCard(dashboard.focusAreas, onClick = { onIntent(HomeIntent.FocusAreaClicked(it)) })
                }
            }
        }
        dashboard.upcomingMock?.let { mock ->
            item {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Coming up")
                    UpcomingMockCard(mock, onRegister = { onIntent(HomeIntent.RegisterMockClicked) })
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    name: String,
    initials: String,
    streakDays: Int,
    onAvatarClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(greeting, style = PulseType.BodySmall.copy(color = TextSecondary))
            Text(name, style = PulseType.Question.copy(color = Ink))
        }
        Row(
            Modifier
                .clip(CircleShape)
                .background(CoralSoft)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(painterResource(R.drawable.ic_flame), contentDescription = "Streak", tint = Coral, modifier = Modifier.size(18.dp))
            Text("$streakDays", style = PulseType.BodyStrong.copy(color = Ink, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold))
        }
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(LilacSoft)
                .clickable(role = Role.Button, onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, style = PulseType.BodyStrong.copy(color = Lilac, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold))
        }
    }
}

@Preview(showBackground = true, heightDp = 1220)
@Composable
private fun HomeScreenPreview() {
    val dashboard = runBlocking { FakeHomeRepository().observeDashboard().first() }
    PhysioExamPrepTheme {
        HomeScreen(state = HomeState(isLoading = false, dashboard = dashboard), onIntent = {})
    }
}
