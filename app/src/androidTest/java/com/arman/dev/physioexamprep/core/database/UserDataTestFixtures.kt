package com.arman.dev.physioexamprep.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.database.entity.ChapterEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity
import com.arman.dev.physioexamprep.core.database.entity.SubjectEntity
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity
import org.junit.Assert.fail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Controllable clock; dates are formatted in UTC so tests don't depend on device time zone. */
class FakeTimeProvider(var now: Long = 1_700_000_000_000L) : TimeProvider {
    override fun nowMillis(): Long = now
    override fun localDate(epochMillis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(epochMillis))

    fun advanceSeconds(seconds: Long) { now += seconds * 1000 }
}

fun createInMemoryDatabase(): AppDatabase =
    Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java)
        .build()

/** Test-only content (not app content). */
object Content {
    const val SUBJECT = 1L
    const val CHAPTER_A = 10L
    const val CHAPTER_B = 11L

    /** Single-correct, chapter A. Correct option: Q_SINGLE_B. */
    const val Q_SINGLE = 100L
    const val Q_SINGLE_A = 1000L
    const val Q_SINGLE_B = 1001L
    const val Q_SINGLE_C = 1002L

    /** Multiple-correct, chapter A. Correct options: Q_MULTI_A + Q_MULTI_B. */
    const val Q_MULTI = 101L
    const val Q_MULTI_A = 1010L
    const val Q_MULTI_B = 1011L
    const val Q_MULTI_C = 1012L

    /** Single-correct, chapter B. Correct option: Q_OTHER_A. */
    const val Q_OTHER = 102L
    const val Q_OTHER_A = 1020L
    const val Q_OTHER_B = 1021L

    /** Question not used by any session (safe to delete). */
    const val Q_UNUSED = 103L
}

suspend fun AppDatabase.seedContent() {
    subjectDao().upsertSubjects(listOf(SubjectEntity(Content.SUBJECT, "Subject", null, 1)))
    chapterDao().upsertChapters(
        listOf(
            ChapterEntity(Content.CHAPTER_A, Content.SUBJECT, "Chapter A", null, 1),
            ChapterEntity(Content.CHAPTER_B, Content.SUBJECT, "Chapter B", null, 2)
        )
    )
    fun q(id: Long, chapter: Long, type: String) = QuestionEntity(
        id = id, chapterId = chapter, questionText = "Q$id", questionType = type,
        difficulty = QuestionEntity.Difficulty.EASY, explanation = null, imagePath = null,
        contentVersion = 1, updatedAt = 0
    )
    questionDao().upsertQuestions(
        listOf(
            q(Content.Q_SINGLE, Content.CHAPTER_A, QuestionEntity.QuestionType.SINGLE_CORRECT),
            q(Content.Q_MULTI, Content.CHAPTER_A, QuestionEntity.QuestionType.MULTIPLE_CORRECT),
            q(Content.Q_OTHER, Content.CHAPTER_B, QuestionEntity.QuestionType.SINGLE_CORRECT),
            q(Content.Q_UNUSED, Content.CHAPTER_B, QuestionEntity.QuestionType.SINGLE_CORRECT)
        )
    )
    fun o(id: Long, question: Long, correct: Boolean, order: Int) = QuestionOptionEntity(id, question, "O$id", correct, order)
    questionOptionDao().upsertOptions(
        listOf(
            o(Content.Q_SINGLE_A, Content.Q_SINGLE, false, 1),
            o(Content.Q_SINGLE_B, Content.Q_SINGLE, true, 2),
            o(Content.Q_SINGLE_C, Content.Q_SINGLE, false, 3),
            o(Content.Q_MULTI_A, Content.Q_MULTI, true, 1),
            o(Content.Q_MULTI_B, Content.Q_MULTI, true, 2),
            o(Content.Q_MULTI_C, Content.Q_MULTI, false, 3),
            o(Content.Q_OTHER_A, Content.Q_OTHER, true, 1),
            o(Content.Q_OTHER_B, Content.Q_OTHER, false, 2),
            o(1030L, Content.Q_UNUSED, true, 1)
        )
    )
}

const val USER = "user-1"

suspend fun AppDatabase.seedUser(userId: String = USER) {
    userProfileDao().insertOrUpdateProfile(UserProfileEntity(userId, "Aditi", null, null, 20, 0, 0))
}

/** Asserts that [block] throws [T] (works with suspend code, unlike assertThrows). */
suspend inline fun <reified T : Throwable> expectFailure(block: () -> Unit): T {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) return e
        fail("Expected ${T::class.java.simpleName} but got $e")
    }
    fail("Expected ${T::class.java.simpleName} but nothing was thrown")
    throw IllegalStateException()
}
