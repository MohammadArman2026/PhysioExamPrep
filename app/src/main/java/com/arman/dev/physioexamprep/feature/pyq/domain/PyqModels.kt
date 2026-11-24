package com.arman.dev.physioexamprep.feature.pyq.domain

import kotlinx.coroutines.flow.Flow

data class ExamPapers(
    val examId: String,
    /** Two-letter monogram, e.g. "AI". */
    val monogram: String,
    val name: String,
    val description: String,
    val tone: ExamTone,
    /** Newest first. */
    val papers: List<PastPaper>,
) {
    val solvedCount: Int get() = papers.count { it.isSolved }
}

data class PastPaper(val id: String, val year: Int, val questionCount: Int, val isSolved: Boolean)

/** Monogram colour pair; mapped to theme colours in the UI. */
enum class ExamTone { INK, CORAL, SKY, AMBER, LILAC, LIME }

/** PYQ question counts per subject, for the "By topic" tab. */
data class PyqTopicCount(val subjectId: Long, val subjectName: String, val questionCount: Int)

enum class PyqGrouping(val label: String) { BY_EXAM("By exam"), BY_TOPIC("By topic"), BY_YEAR("By year") }

interface PyqRepository {
    fun observeExams(): Flow<List<ExamPapers>>
    fun observeTopicCounts(): Flow<List<PyqTopicCount>>
}
