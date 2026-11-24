package com.arman.dev.physioexamprep.feature.profile.data

import com.arman.dev.physioexamprep.feature.profile.domain.ProfileOverview
import com.arman.dev.physioexamprep.feature.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Data from the Figma "09 · Profile" frame. Activity levels are a made-up but stable pattern. */
@Singleton
class FakeProfileRepository @Inject constructor() : ProfileRepository {

    private val activity: List<List<Int>> = List(12) { week ->
        List(7) { day -> ((week * 7 + day) * 37 % 11).let { v -> if (week >= 8) (v % 3) + 1 else v % 4 } }
    }

    private val profile = MutableStateFlow(
        ProfileOverview(
            name = "Aditi Sharma",
            subtitle = "Final year · BPT, Delhi",
            targetExam = "AIIMS 2027",
            questionsSolved = 2450,
            percentile = 88,
            streakDays = 12,
            activity = activity,
            reminderTime = "7:00 PM",
            darkMode = false,
        )
    )

    override fun observeProfile(): Flow<ProfileOverview> = profile.asStateFlow()

    override suspend fun setDarkMode(enabled: Boolean) = profile.update { it.copy(darkMode = enabled) }
}
