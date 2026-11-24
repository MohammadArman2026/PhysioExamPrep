package com.arman.dev.physioexamprep.core.domain.model

/**
 * What to start in the Quiz feature. Shared contract: Home, Topic picker, PYQs and Mocks build
 * one of these and navigation turns it into the quiz route (see navigation/Routes.kt).
 */
data class QuizRequest(
    val source: QuizSource,
    val questionCount: Int,
    val mode: PracticeMode = PracticeMode.PRACTICE,
    val subjectId: Long? = null,
    val topicIds: List<Long> = emptyList(),
    /** Mock id, PYQ paper id ("AIIMS-2023") or resume id, depending on [source]. */
    val refId: String? = null,
)

enum class QuizSource { PRACTICE, RESUME, QUICK_MIX, PYQ, MOCK }

/** Topic picker "Practice / Timed / Exam mode" toggle. */
enum class PracticeMode(val label: String) {
    PRACTICE("Practice"),
    TIMED("Timed"),
    EXAM("Exam mode");

    /** Practice & Timed check each answer right away; Exam mode only at the end. */
    val showsInstantFeedback: Boolean get() = this != EXAM
}
