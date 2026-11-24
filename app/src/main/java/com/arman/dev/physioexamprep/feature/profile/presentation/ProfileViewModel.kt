package com.arman.dev.physioexamprep.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.arman.dev.physioexamprep.core.mvi.MviViewModel
import com.arman.dev.physioexamprep.feature.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    init {
        viewModelScope.launch {
            repository.observeProfile().collect { profile -> setState { copy(isLoading = false, profile = profile) } }
        }
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.EditClicked -> soon("Editing your profile")
            ProfileIntent.SettingsClicked -> soon("Settings")
            ProfileIntent.RemindersClicked -> soon("Reminder settings")
            is ProfileIntent.DarkModeToggled -> viewModelScope.launch { repository.setDarkMode(intent.enabled) }
            ProfileIntent.ExamTargetClicked -> soon("Changing your exam target")
            ProfileIntent.HelpClicked -> soon("Help & support")
            ProfileIntent.LogoutClicked -> setState { copy(showLogoutDialog = true) }
            ProfileIntent.LogoutDismissed -> setState { copy(showLogoutDialog = false) }
            ProfileIntent.LogoutConfirmed -> {
                setState { copy(showLogoutDialog = false) }
                sendEffect(ProfileEffect.LoggedOut)
            }
        }
    }

    private fun soon(what: String) = sendEffect(ProfileEffect.ShowMessage("$what is coming soon"))
}
