package com.arman.dev.physioexamprep.core.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arman.dev.physioexamprep.core.data.repository.AssessmentRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.BookmarkRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.UserProfileRepositoryImpl
import com.arman.dev.physioexamprep.core.database.migration.ALL_MIGRATIONS
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Builds a real version-1 database file (content tables only, exactly as Room v1
 * created them), then opens it with the current AppDatabase + migrations.
 * Room validates every table, column, FK and index after migrating, so a mismatch
 * between MIGRATION_1_2 and the entities fails this test.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dbName = "migration-test.db"

    @Before
    fun setUp() {
        context.deleteDatabase(dbName)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(dbName)
    }

    private fun createVersion1Database() {
        val file = context.getDatabasePath(dbName).apply { parentFile?.mkdirs() }
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            V1_SCHEMA.forEach(db::execSQL)
            db.execSQL("INSERT INTO subjects VALUES (1, 'Anatomy', NULL, 1, 1)")
            db.execSQL("INSERT INTO chapters VALUES (10, 1, 'Upper Limb', NULL, 1)")
            db.execSQL(
                "INSERT INTO questions VALUES (100, 10, 'Which muscle initiates abduction?', " +
                    "'SINGLE_CORRECT', 'EASY', NULL, NULL, 1, 0, 1)"
            )
            db.execSQL("INSERT INTO question_options VALUES (1000, 100, 'Deltoid', 0, 1)")
            db.execSQL("INSERT INTO question_options VALUES (1001, 100, 'Supraspinatus', 1, 2)")
            db.version = 1
        }
    }

    private fun openCurrent(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(*ALL_MIGRATIONS)
            .build()

    @Test
    fun migrate1To2_preservesContent_andUserTablesWork() = runTest {
        createVersion1Database()

        val db = openCurrent()
        // Opening triggers MIGRATION_1_2 and Room's schema validation.
        assertEquals(listOf(1L), db.subjectDao().getAllActiveSubjects().first().map { it.id })
        assertEquals(listOf(10L), db.chapterDao().getChaptersBySubject(1).first().map { it.id })
        assertEquals(1, db.questionDao().getTotalQuestionsBySubject(1).first())
        assertEquals(listOf(1000L, 1001L), db.questionOptionDao().getOptionsByQuestion(100).first().map { it.id })

        // New user tables accept data that references migrated content.
        val time = FakeTimeProvider()
        UserProfileRepositoryImpl(db, time).saveProfile(USER, "Aditi", null, null)
        BookmarkRepositoryImpl(db, time).addBookmark(USER, 100)
        val assessments = AssessmentRepositoryImpl(db, time)
        val sessionId = assessments.startSession(USER, SessionType.PRACTICE, "P", listOf(QuestionSelection(100)), null)
        val sq = assessments.observeSession(sessionId).first()!!.questions.single().sessionQuestionId
        assertTrue(assessments.saveAnswer(sq, setOf(1001L))!!.isCorrect)

        db.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
        db.close()

        // Reopening (no migration needed now) keeps both content and user data.
        val reopened = openCurrent()
        assertEquals(1, reopened.questionDao().getTotalQuestionsBySubject(1).first())
        assertEquals(listOf(100L), reopened.questionBookmarkDao().observeBookmarkedQuestionIds(USER).first())
        assertEquals(1, reopened.questionProgressDao().getQuestionProgress(USER, 100)!!.correctCount)
        assertEquals(1, reopened.assessmentSessionDao().observeSessions(USER).first().size)
        reopened.close()
    }

    private companion object {
        /** Version-1 schema exactly as Room generated it for the content entities. */
        val V1_SCHEMA = listOf(
            "CREATE TABLE IF NOT EXISTS `subjects` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `displayOrder` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE INDEX IF NOT EXISTS `index_subjects_isActive_displayOrder` ON `subjects` (`isActive`, `displayOrder`)",
            "CREATE TABLE IF NOT EXISTS `chapters` (`id` INTEGER NOT NULL, `subjectId` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `displayOrder` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`subjectId`) REFERENCES `subjects`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            "CREATE INDEX IF NOT EXISTS `index_chapters_subjectId` ON `chapters` (`subjectId`)",
            "CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER NOT NULL, `chapterId` INTEGER NOT NULL, `questionText` TEXT NOT NULL, `questionType` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `explanation` TEXT, `imagePath` TEXT, `contentVersion` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`chapterId`) REFERENCES `chapters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            "CREATE INDEX IF NOT EXISTS `index_questions_chapterId` ON `questions` (`chapterId`)",
            "CREATE INDEX IF NOT EXISTS `index_questions_difficulty` ON `questions` (`difficulty`)",
            "CREATE INDEX IF NOT EXISTS `index_questions_isActive` ON `questions` (`isActive`)",
            "CREATE TABLE IF NOT EXISTS `question_options` (`id` INTEGER NOT NULL, `questionId` INTEGER NOT NULL, `optionText` TEXT NOT NULL, `isCorrect` INTEGER NOT NULL, `displayOrder` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
            "CREATE INDEX IF NOT EXISTS `index_question_options_questionId` ON `question_options` (`questionId`)"
        )
    }
}
