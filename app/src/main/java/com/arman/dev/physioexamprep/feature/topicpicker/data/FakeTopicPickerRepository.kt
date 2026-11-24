package com.arman.dev.physioexamprep.feature.topicpicker.data

import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.topicpicker.domain.SubjectDetail
import com.arman.dev.physioexamprep.feature.topicpicker.domain.TopicPickerRepository
import com.arman.dev.physioexamprep.feature.topicpicker.domain.TopicProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Anatomy matches the Figma "03 · Topic picker" frame exactly. The other subjects' topic lists
 * are placeholders so every Learn tile opens something sensible.
 */
@Singleton
class FakeTopicPickerRepository @Inject constructor() : TopicPickerRepository {

    private val subjects = MutableStateFlow(
        listOf(
            SubjectDetail(
                SubjectIds.ANATOMY, "Anatomy", completePercent = 45, accuracyPercent = 78, timeSpentMinutes = 260, isBookmarked = false,
                topics = listOf(
                    TopicProgress(101, "Upper Limb", 120, 82),
                    TopicProgress(102, "Lower Limb", 145, 64),
                    TopicProgress(104, "Thorax", 90, 41),
                    TopicProgress(105, "Abdomen", 110, null),
                    TopicProgress(106, "Pelvis & Perineum", 80, 58),
                    TopicProgress(107, "Head & Neck", 70, null),
                    TopicProgress(108, "Neuroanatomy", 160, 39),
                    TopicProgress(103, "Spine", 95, 71),
                    TopicProgress(109, "Embryology", 85, null),
                    TopicProgress(110, "Histology", 100, 52),
                    TopicProgress(111, "Osteology", 105, 66),
                    TopicProgress(112, "Surface anatomy", 80, null),
                ),
                suggestedTopicIds = setOf(101L, 104L, 106L),
            ),
            SubjectDetail(
                SubjectIds.PHYSIOLOGY, "Physiology", 62, 74, 310, false,
                listOf(
                    TopicProgress(501, "Nerve & muscle", 120, 81),
                    TopicProgress(502, "Cardiovascular", 130, 70),
                    TopicProgress(503, "Respiratory", 110, 66),
                    TopicProgress(504, "Renal", 90, 48),
                    TopicProgress(505, "Endocrine", 100, null),
                ),
            ),
            SubjectDetail(
                SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 30, 69, 140, false,
                listOf(
                    TopicProgress(201, "Fundamentals", 120, 77),
                    TopicProgress(202, "Stretching", 110, 61),
                    TopicProgress(203, "Strengthening", 130, 55),
                    TopicProgress(204, "Gait cycle", 90, 61),
                    TopicProgress(205, "Hydrotherapy", 80, null),
                ),
            ),
            SubjectDetail(
                SubjectIds.ELECTROTHERAPY, "Electrotherapy", 12, 52, 45, false,
                listOf(
                    TopicProgress(301, "TENS & IFT", 110, 48),
                    TopicProgress(302, "Ultrasound", 100, 57),
                    TopicProgress(303, "Shortwave diathermy", 90, null),
                    TopicProgress(304, "Laser", 70, null),
                ),
            ),
            SubjectDetail(
                SubjectIds.ORTHOPEDICS, "Orthopedics", 8, 70, 30, false,
                listOf(
                    TopicProgress(401, "Fractures", 140, 72),
                    TopicProgress(402, "Spine conditions", 120, null),
                    TopicProgress(403, "Sports injuries", 110, null),
                    TopicProgress(404, "Arthroplasty", 90, null),
                ),
            ),
            SubjectDetail(
                SubjectIds.NEUROLOGY, "Neurology", 0, 0, 0, false,
                listOf(
                    TopicProgress(601, "Cranial nerves", 140, 56),
                    TopicProgress(602, "Stroke rehab", 160, null),
                    TopicProgress(603, "Spinal cord injury", 120, null),
                    TopicProgress(604, "Parkinson's", 100, null),
                ),
            ),
        )
    )

    override fun observeSubject(subjectId: Long): Flow<SubjectDetail?> =
        subjects.map { list -> list.firstOrNull { it.id == subjectId } }

    override suspend fun setBookmarked(subjectId: Long, bookmarked: Boolean) {
        subjects.update { list -> list.map { if (it.id == subjectId) it.copy(isBookmarked = bookmarked) else it } }
    }
}
