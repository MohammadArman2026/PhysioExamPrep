package com.arman.dev.physioexamprep.feature.home.data

import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.home.domain.DailyGoal
import com.arman.dev.physioexamprep.feature.home.domain.FocusArea
import com.arman.dev.physioexamprep.feature.home.domain.HomeDashboard
import com.arman.dev.physioexamprep.feature.home.domain.HomeRepository
import com.arman.dev.physioexamprep.feature.home.domain.PyqOfTheDay
import com.arman.dev.physioexamprep.feature.home.domain.QuickMix
import com.arman.dev.physioexamprep.feature.home.domain.ResumeItem
import com.arman.dev.physioexamprep.feature.home.domain.UpcomingMock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** In-memory data matching the Figma "01 · Home" frame. Swap for a Room-backed impl later. */
@Singleton
class FakeHomeRepository @Inject constructor() : HomeRepository {

    private val dashboard = MutableStateFlow(
        HomeDashboard(
            userName = "Aditi Sharma",
            streakDays = 12,
            dailyGoal = DailyGoal(answered = 32, target = 50),
            resume = ResumeItem(
                id = "resume-upper-limb",
                subjectId = SubjectIds.ANATOMY,
                subjectName = "Anatomy",
                topicId = 101L,
                topicName = "Upper Limb",
                subtopicName = "Brachial plexus",
                questionsLeft = 12,
                progress = 0.7f,
            ),
            quickMix = QuickMix(questionCount = 10, approxMinutes = 8),
            pyqOfTheDay = PyqOfTheDay(paperId = "AIIMS-2023", exam = "AIIMS", year = 2023, questionNumber = 47),
            focusAreas = listOf(
                FocusArea(SubjectIds.ELECTROTHERAPY, "Electrotherapy", "TENS & IFT", 48),
                FocusArea(SubjectIds.NEUROLOGY, "Neurology", "Cranial nerves", 56),
                FocusArea(SubjectIds.EXERCISE_THERAPY, "Biomechanics", "Gait cycle", 61),
            ),
            upcomingMock = UpcomingMock(
                id = "aim-4",
                title = "All‑India Mock #4",
                startsAtLabel = "SAT, 7 PM",
                registeredCount = 2340,
                isRegistered = false,
            ),
        )
    )

    override fun observeDashboard(): Flow<HomeDashboard> = dashboard.asStateFlow()

    override suspend fun registerForMock(mockId: String) {
        dashboard.update { d ->
            val mock = d.upcomingMock ?: return@update d
            if (mock.id != mockId || mock.isRegistered) d
            else d.copy(upcomingMock = mock.copy(isRegistered = true, registeredCount = mock.registeredCount + 1))
        }
    }
}
