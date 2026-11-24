package com.arman.dev.physioexamprep.feature.topicpicker.domain

import kotlinx.coroutines.flow.Flow

data class SubjectDetail(
    val id: Long,
    val name: String,
    /** 0..100 */
    val completePercent: Int,
    /** 0..100 */
    val accuracyPercent: Int,
    val timeSpentMinutes: Int,
    val isBookmarked: Boolean,
    val topics: List<TopicProgress>,
    /** Topics ticked when the screen opens (e.g. the user's last selection). */
    val suggestedTopicIds: Set<Long> = emptySet(),
) {
    val questionCount: Int get() = topics.sumOf { it.questionCount }
}

data class TopicProgress(
    val id: Long,
    val name: String,
    val questionCount: Int,
    /** null = never practised ("— accuracy", "New" pill). */
    val accuracyPercent: Int?,
) {
    val mastery: Mastery get() = Mastery.from(accuracyPercent)
}

/** Pill on each topic row. Thresholds are a guess from the Figma samples (82 Strong, 64/58 Fair, 41/39 Weak). */
enum class Mastery(val label: String) {
    STRONG("Strong"), FAIR("Fair"), WEAK("Weak"), NEW("New");

    companion object {
        fun from(accuracy: Int?): Mastery = when {
            accuracy == null -> NEW
            accuracy >= 75 -> STRONG
            accuracy >= 50 -> FAIR
            else -> WEAK
        }
    }
}

interface TopicPickerRepository {
    /** Emits null if the subject doesn't exist. */
    fun observeSubject(subjectId: Long): Flow<SubjectDetail?>
    suspend fun setBookmarked(subjectId: Long, bookmarked: Boolean)
}
