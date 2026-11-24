package com.arman.dev.physioexamprep.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.Danger
import com.arman.dev.physioexamprep.core.ui.theme.Ink
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.core.ui.theme.PulseType
import com.arman.dev.physioexamprep.feature.profile.data.FakeProfileRepository
import com.arman.dev.physioexamprep.feature.profile.presentation.components.ActivityCard
import com.arman.dev.physioexamprep.feature.profile.presentation.components.IdentityCard
import com.arman.dev.physioexamprep.feature.profile.presentation.components.ProfileStats
import com.arman.dev.physioexamprep.feature.profile.presentation.components.SettingsCard
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileRoute(
    onLoggedOut: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is ProfileEffect.ShowMessage -> Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
            ProfileEffect.LoggedOut -> onLoggedOut()
        }
    }
    ProfileScreen(state, viewModel::onIntent, contentPadding)
}

/** Stateless UI for "09 · Profile". */
@Composable
fun ProfileScreen(state: ProfileState, onIntent: (ProfileIntent) -> Unit, contentPadding: PaddingValues = PaddingValues()) {
    val profile = state.profile
    if (profile == null) {
        Box(Modifier.fillMaxSize().background(AppCanvas), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Ink) }
        return
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(AppCanvas)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Profile", style = PulseType.ScreenTitle.copy(color = Ink), modifier = Modifier.weight(1f))
                HeaderIconButton(R.drawable.ic_sliders, "Settings", onClick = { onIntent(ProfileIntent.SettingsClicked) })
            }
        }
        item { IdentityCard(profile, state.initials, onEdit = { onIntent(ProfileIntent.EditClicked) }) }
        item { ProfileStats(profile) }
        item { ActivityCard(profile.activity) }
        item { SettingsCard(profile, onIntent) }
    }

    if (state.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { onIntent(ProfileIntent.LogoutDismissed) },
            title = { Text("Log out?", style = PulseType.SheetTitle) },
            text = { Text("You can log back in any time.", style = PulseType.Body) },
            confirmButton = {
                TextButton(onClick = { onIntent(ProfileIntent.LogoutConfirmed) }) { Text("Log out", color = Danger) }
            },
            dismissButton = { TextButton(onClick = { onIntent(ProfileIntent.LogoutDismissed) }) { Text("Cancel") } },
            containerColor = AppCanvas,
        )
    }
}

@Preview(showBackground = true, heightDp = 1136)
@Composable
private fun ProfilePreview() {
    val profile = runBlocking { FakeProfileRepository().observeProfile().first() }
    PhysioExamPrepTheme { ProfileScreen(ProfileState(false, profile), onIntent = {}) }
}
