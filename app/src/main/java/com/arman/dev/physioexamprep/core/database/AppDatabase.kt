package com.arman.dev.physioexamprep.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arman.dev.physioexamprep.core.database.dao.ChapterDao
import com.arman.dev.physioexamprep.core.database.dao.QuestionDao
import com.arman.dev.physioexamprep.core.database.dao.QuestionOptionDao
import com.arman.dev.physioexamprep.core.database.dao.SubjectDao
import com.arman.dev.physioexamprep.core.database.dao.user.AssessmentSessionDao
import com.arman.dev.physioexamprep.core.database.dao.user.ChapterProgressDao
import com.arman.dev.physioexamprep.core.database.dao.user.DailyActivityDao
import com.arman.dev.physioexamprep.core.database.dao.user.MockDefinitionDao
import com.arman.dev.physioexamprep.core.database.dao.user.QuestionBookmarkDao
import com.arman.dev.physioexamprep.core.database.dao.user.QuestionProgressDao
import com.arman.dev.physioexamprep.core.database.dao.user.SessionAnswerDao
import com.arman.dev.physioexamprep.core.database.dao.user.SessionQuestionDao
import com.arman.dev.physioexamprep.core.database.dao.user.SessionResultDao
import com.arman.dev.physioexamprep.core.database.dao.user.UserProfileDao
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity
import com.arman.dev.physioexamprep.core.database.entity.SubjectEntity
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.ChapterProgressEntity
import com.arman.dev.physioexamprep.core.database.entity.user.DailyActivityEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionQuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionBookmarkEntity
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionProgressEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerOptionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionResultEntity
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity

/**
 * Single Room database for the app (content + user data).
 *
 * Version history:
 *  1 – initial schema: content tables (subjects → chapters → questions → question_options)
 *      and user tables (profiles, mock definitions, assessment sessions/answers/results,
 *      bookmarks, progress, daily activity). Shipped pre-populated via assets/physio_exam_prep.db.
 *
 * Schema JSON is exported to app/schemas/ (commit it). For every schema change,
 * bump [version] and add a migration – never use fallbackToDestructiveMigration(),
 * because user progress lives in this database.
 */
@Database(
    entities = [
        // Content
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        // User data
        UserProfileEntity::class,
        MockDefinitionEntity::class,
        MockDefinitionQuestionEntity::class,
        AssessmentSessionEntity::class,
        SessionQuestionEntity::class,
        SessionAnswerEntity::class,
        SessionAnswerOptionEntity::class,
        SessionResultEntity::class,
        QuestionBookmarkEntity::class,
        QuestionProgressEntity::class,
        ChapterProgressEntity::class,
        DailyActivityEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    // Content
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionOptionDao(): QuestionOptionDao

    // User data
    abstract fun userProfileDao(): UserProfileDao
    abstract fun mockDefinitionDao(): MockDefinitionDao
    abstract fun assessmentSessionDao(): AssessmentSessionDao
    abstract fun sessionQuestionDao(): SessionQuestionDao
    abstract fun sessionAnswerDao(): SessionAnswerDao
    abstract fun sessionResultDao(): SessionResultDao
    abstract fun questionBookmarkDao(): QuestionBookmarkDao
    abstract fun questionProgressDao(): QuestionProgressDao
    abstract fun chapterProgressDao(): ChapterProgressDao
    abstract fun dailyActivityDao(): DailyActivityDao

    companion object {
        const val DATABASE_NAME = "physio_exam_prep.db"
    }
}
