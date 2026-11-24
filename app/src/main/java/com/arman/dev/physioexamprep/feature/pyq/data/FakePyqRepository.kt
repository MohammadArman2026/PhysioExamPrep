package com.arman.dev.physioexamprep.feature.pyq.data

import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.pyq.domain.ExamPapers
import com.arman.dev.physioexamprep.feature.pyq.domain.ExamTone
import com.arman.dev.physioexamprep.feature.pyq.domain.PastPaper
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqRepository
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqTopicCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/** First four exams match the Figma "06 · Past papers" frame; the other five are placeholders ("9 exams"). */
@Singleton
class FakePyqRepository @Inject constructor() : PyqRepository {

    private fun papers(exam: String, total: Int, solvedYears: Set<Int>) =
        (0 until total).map { i ->
            val year = 2024 - i
            PastPaper("$exam-$year", year, 100, year in solvedYears)
        }

    private val exams = listOf(
        ExamPapers("AIIMS", "AI", "AIIMS", "M.Sc. Physiotherapy entrance", ExamTone.INK, papers("AIIMS", 12, setOf(2024, 2023, 2019))),
        ExamPapers("PGIMER", "PG", "PGIMER", "MPT entrance · Chandigarh", ExamTone.CORAL, papers("PGIMER", 10, setOf(2024))),
        ExamPapers("JIPMER", "JP", "JIPMER", "MPT entrance · Puducherry", ExamTone.SKY, papers("JIPMER", 11, emptySet())),
        ExamPapers("BHU", "BH", "BHU", "PET · Banaras Hindu University", ExamTone.AMBER, papers("BHU", 8, setOf(2024, 2023))),
        ExamPapers("CMC", "CM", "CMC Vellore", "MPT entrance", ExamTone.LILAC, papers("CMC", 6, emptySet())),
        ExamPapers("MAHE", "MA", "MAHE Manipal", "MPT entrance", ExamTone.LIME, papers("MAHE", 7, emptySet())),
        ExamPapers("IPU", "IP", "GGSIPU", "MPT CET · Delhi", ExamTone.CORAL, papers("IPU", 6, emptySet())),
        ExamPapers("JH", "JH", "Jamia Hamdard", "MPT entrance · Delhi", ExamTone.SKY, papers("JH", 5, emptySet())),
        ExamPapers("KGMU", "KG", "KGMU", "MPT entrance · Lucknow", ExamTone.INK, papers("KGMU", 5, emptySet())),
    )

    override fun observeExams(): Flow<List<ExamPapers>> = flowOf(exams)

    override fun observeTopicCounts(): Flow<List<PyqTopicCount>> = flowOf(
        listOf(
            PyqTopicCount(SubjectIds.ANATOMY, "Anatomy", 640),
            PyqTopicCount(SubjectIds.PHYSIOLOGY, "Physiology", 520),
            PyqTopicCount(SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 480),
            PyqTopicCount(SubjectIds.ELECTROTHERAPY, "Electrotherapy", 410),
            PyqTopicCount(SubjectIds.ORTHOPEDICS, "Orthopedics", 530),
            PyqTopicCount(SubjectIds.NEUROLOGY, "Neurology", 590),
        )
    )
}
