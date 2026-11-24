package com.arman.dev.physioexamprep.feature.home.domain

data class HomeDashboard(
    val userName: String,
    val streakDays: Int,
    val dailyGoal: DailyGoal,
    val resume: ResumeItem?,
    val quickMix: QuickMix,
    val pyqOfTheDay: PyqOfTheDay?,
    val focusAreas: List<FocusArea>,
    val upcomingMock: UpcomingMock?,
)

data class DailyGoal(val answered: Int, val target: Int) {
    val remaining: Int get() = (target - answered).coerceAtLeast(0)
    val fraction: Float get() = if (target == 0) 0f else (answered.toFloat() / target).coerceIn(0f, 1f)
}

/** "Pick up where you left off" card. */
data class ResumeItem(
    val id: String,
    val subjectId: Long,
    val subjectName: String,
    val topicId: Long,
    val topicName: String,
    val subtopicName: String,
    val questionsLeft: Int,
    /** 0..1 */
    val progress: Float,
)

data class QuickMix(val questionCount: Int, val approxMinutes: Int)

data class PyqOfTheDay(val paperId: String, val exam: String, val year: Int, val questionNumber: Int)

/** Weak topic row under "Focus areas". */
data class FocusArea(
    val subjectId: Long,
    val subjectName: String,
    val topicName: String,
    /** 0..100 */
    val accuracyPercent: Int,
)

data class UpcomingMock(
    val id: String,
    val title: String,
    /** Already formatted for the overline, e.g. "SAT, 7 PM". */
    val startsAtLabel: String,
    val registeredCount: Int,
    val isRegistered: Boolean,
)
