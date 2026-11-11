package com.arman.dev.physioexamprep.core.data.repository

import com.arman.dev.physioexamprep.core.data.mapper.toDomain
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.domain.model.ChapterProgress
import com.arman.dev.physioexamprep.core.domain.model.DailyActivity
import com.arman.dev.physioexamprep.core.domain.model.QuestionProgress
import com.arman.dev.physioexamprep.core.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    database: AppDatabase
) : ProgressRepository {

    private val questionProgressDao = database.questionProgressDao()
    private val chapterProgressDao = database.chapterProgressDao()
    private val dailyActivityDao = database.dailyActivityDao()

    override fun observeQuestionProgress(userId: String, questionId: Long): Flow<QuestionProgress?> =
        questionProgressDao.observeQuestionProgress(userId, questionId).map { it?.toDomain() }

    override fun observeChapterProgress(userId: String, chapterId: Long): Flow<ChapterProgress?> =
        chapterProgressDao.observeChapterProgress(userId, chapterId).map { it?.toDomain() }

    override fun observeChapterProgressForSubject(userId: String, subjectId: Long): Flow<List<ChapterProgress>> =
        chapterProgressDao.observeChapterProgressForSubject(userId, subjectId).map { list -> list.map { it.toDomain() } }

    override fun observeActivityHistory(userId: String, fromDate: String): Flow<List<DailyActivity>> =
        dailyActivityDao.observeActivityHistory(userId, fromDate).map { list -> list.map { it.toDomain() } }

    override suspend fun getActivityForDate(userId: String, activityDate: String): DailyActivity? =
        dailyActivityDao.getActivityForDate(userId, activityDate)?.toDomain()
}
