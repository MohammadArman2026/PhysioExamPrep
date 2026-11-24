package com.arman.dev.physioexamprep.feature.profile.presentation

import com.arman.dev.physioexamprep.core.mvi.UiEffect
import com.arman.dev.physioexamprep.core.mvi.UiIntent
import com.arman.dev.physioexamprep.core.mvi.UiState
import com.arman.dev.physioexamprep.feature.profile.domain.ProfileOverview

data class ProfileState(
    val isLoading: Boolean = true,
    val profile: ProfileOverview? = null,
    val showLogoutDialog: Boolean = false,
) : UiState {
    val initials: String
        get() = profile?.name.orEmpty().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}

sealed interface ProfileIntent : UiIntent {
    data object EditClicked : ProfileIntent
    data object SettingsClicked : ProfileIntent
    data object RemindersClicked : ProfileIntent
    data class DarkModeToggled(val enabled: Boolean) : ProfileIntent
    data object ExamTargetClicked : ProfileIntent
    data object HelpClicked : ProfileIntent
    data object LogoutClicked : ProfileIntent
    data object LogoutConfirmed : ProfileIntent
    data object LogoutDismissed : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data class ShowMessage(val text: String) : ProfileEffect
    data object LoggedOut : ProfileEffect
}
