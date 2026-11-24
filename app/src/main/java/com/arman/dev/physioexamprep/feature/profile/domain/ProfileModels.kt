package com.arman.dev.physioexamprep.feature.profile.domain

import kotlinx.coroutines.flow.Flow

data class ProfileOverview(
    val name: String,
    /** "Final year · BPT, Delhi" */
    val subtitle: String,
    val targetExam: String,
    val questionsSolved: Int,
    val percentile: Int,
    val streakDays: Int,
    /** 12 weeks × 7 days, oldest week first; each value 0..3 (none → most). */
    val activity: List<List<Int>>,
    val reminderTime: String,
    val darkMode: Boolean,
)

interface ProfileRepository {
    fun observeProfile(): Flow<ProfileOverview>
    suspend fun setDarkMode(enabled: Boolean)
}
