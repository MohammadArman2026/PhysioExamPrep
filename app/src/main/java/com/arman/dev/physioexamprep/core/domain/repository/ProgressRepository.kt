package com.arman.dev.physioexamprep.core.domain.repository

import com.arman.dev.physioexamprep.core.domain.model.ChapterProgress
import com.arman.dev.physioexamprep.core.domain.model.DailyActivity
import com.arman.dev.physioexamprep.core.domain.model.QuestionProgress
import kotlinx.coroutines.flow.Flow

/**
 * Read side of progress. Progress is written by [AssessmentRepository] when
 * answers are evaluated, so it can't drift from the attempts that produced it.
 */
interface ProgressRepository {
    fun observeQuestionProgress(userId: String, questionId: Long): Flow<QuestionProgress?>
    fun observeChapterProgress(userId: String, chapterId: Long): Flow<ChapterProgress?>
    fun observeChapterProgressForSubject(userId: String, subjectId: Long): Flow<List<ChapterProgress>>

    /** [fromDate] is YYYY-MM-DD; newest day first. */
    fun observeActivityHistory(userId: String, fromDate: String): Flow<List<DailyActivity>>
    suspend fun getActivityForDate(userId: String, activityDate: String): DailyActivity?
}
