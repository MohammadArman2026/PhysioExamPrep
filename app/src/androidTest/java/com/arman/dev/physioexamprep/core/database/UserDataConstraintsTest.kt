package com.arman.dev.physioexamprep.core.database

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.DailyActivityEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionQuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionBookmarkEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionAnswerOptionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionQuestionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus
import com.arman.dev.physioexamprep.core.domain.model.MockType
import com.arman.dev.physioexamprep.core.domain.model.SessionStatus
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Foreign keys, unique constraints and delete behaviour of the user tables. */
@RunWith(AndroidJUnit4::class)
class UserDataConstraintsTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() = runTest {
        db = createInMemoryDatabase()
        db.seedContent()
        db.seedUser()
    }

    @After
    fun tearDown() = db.close()

    private fun mock(id: String = "mock-1", userId: String = USER) = MockDefinitionEntity(
        id = id, userId = userId, title = "Mock", description = null, mockType = MockType.CUSTOM,
        sourcePaperId = null, durationMinutes = 10, totalQuestions = 1, filterConfig = null,
        createdAt = 0, updatedAt = 0, deletedAt = null
    )

    private fun session(id: String = "s-1", mockId: String? = null) = AssessmentSessionEntity(
        id = id, userId = USER, sessionType = SessionType.MOCK, title = "S", mockDefinitionId = mockId,
        sourcePaperId = null, durationSeconds = null, startedAt = 0, completedAt = null,
        status = SessionStatus.IN_PROGRESS, createdAt = 0, updatedAt = 0, deletedAt = null
    )

    private suspend fun insertSessionWithQuestion(): Pair<String, String> {
        db.assessmentSessionDao().createSession(session())
        db.sessionQuestionDao().insertSessionQuestions(listOf(SessionQuestionEntity("sq-1", "s-1", Content.Q_SINGLE, 0, 1.0, 0.0)))
        db.sessionAnswerDao().insertAnswers(listOf(SessionAnswerEntity("a-1", "sq-1", AnswerStatus.UNANSWERED, answeredAt = null, updatedAt = 0)))
        return "sq-1" to "a-1"
    }

    // ------------------------------------------------------------- foreign keys

    @Test
    fun userData_requiresExistingProfile() = runTest {
        expectFailure<SQLiteConstraintException> { db.mockDefinitionDao().insertMockDefinition(mock(userId = "nobody")) }
    }

    @Test
    fun sessionQuestion_requiresExistingContentQuestion() = runTest {
        db.assessmentSessionDao().createSession(session())
        expectFailure<SQLiteConstraintException> {
            db.sessionQuestionDao().insertSessionQuestions(listOf(SessionQuestionEntity("sq-x", "s-1", 999_999L, 0, 1.0, 0.0)))
        }
    }

    @Test
    fun selectedOption_requiresExistingContentOption() = runTest {
        insertSessionWithQuestion()
        expectFailure<SQLiteConstraintException> {
            db.sessionAnswerDao().insertSelectedOptions(listOf(SessionAnswerOptionEntity("o-1", "a-1", 999_999L)))
        }
    }

    @Test
    fun bookmark_requiresExistingQuestion() = runTest {
        expectFailure<SQLiteConstraintException> {
            db.questionBookmarkDao().insertBookmark(QuestionBookmarkEntity("b-1", USER, 999_999L, 0, 0, null))
        }
    }

    // ------------------------------------------------------------- unique constraints

    @Test
    fun bookmark_isUniquePerUserAndQuestion() = runTest {
        db.questionBookmarkDao().insertBookmark(QuestionBookmarkEntity("b-1", USER, Content.Q_SINGLE, 0, 0, null))
        expectFailure<SQLiteConstraintException> {
            db.questionBookmarkDao().insertBookmark(QuestionBookmarkEntity("b-2", USER, Content.Q_SINGLE, 0, 0, null))
        }
    }

    @Test
    fun sessionQuestion_positionAndQuestionAreUniquePerSession() = runTest {
        insertSessionWithQuestion()
        expectFailure<SQLiteConstraintException> { // same position
            db.sessionQuestionDao().insertSessionQuestions(listOf(SessionQuestionEntity("sq-2", "s-1", Content.Q_MULTI, 0, 1.0, 0.0)))
        }
        expectFailure<SQLiteConstraintException> { // same question
            db.sessionQuestionDao().insertSessionQuestions(listOf(SessionQuestionEntity("sq-3", "s-1", Content.Q_SINGLE, 1, 1.0, 0.0)))
        }
    }

    @Test
    fun sessionAnswer_atMostOnePerSessionQuestion() = runTest {
        insertSessionWithQuestion()
        expectFailure<SQLiteConstraintException> {
            db.sessionAnswerDao().insertAnswers(listOf(SessionAnswerEntity("a-2", "sq-1", AnswerStatus.UNANSWERED, answeredAt = null, updatedAt = 0)))
        }
    }

    @Test
    fun selectedOption_isUniquePerAnswer() = runTest {
        insertSessionWithQuestion()
        db.sessionAnswerDao().insertSelectedOptions(listOf(SessionAnswerOptionEntity("o-1", "a-1", Content.Q_SINGLE_A)))
        expectFailure<SQLiteConstraintException> {
            db.sessionAnswerDao().insertSelectedOptions(listOf(SessionAnswerOptionEntity("o-2", "a-1", Content.Q_SINGLE_A)))
        }
    }

    @Test
    fun mockQuestions_areUniquePerMock() = runTest {
        db.mockDefinitionDao().insertMockDefinition(mock())
        db.mockDefinitionDao().insertMockQuestions(listOf(MockDefinitionQuestionEntity("mq-1", "mock-1", Content.Q_SINGLE, 0, 1.0, 0.0)))
        expectFailure<SQLiteConstraintException> {
            db.mockDefinitionDao().insertMockQuestions(listOf(MockDefinitionQuestionEntity("mq-2", "mock-1", Content.Q_SINGLE, 1, 1.0, 0.0)))
        }
    }

    @Test
    fun dailyActivity_isUniquePerUserAndDate() = runTest {
        db.dailyActivityDao().insertDailyActivity(DailyActivityEntity("d-1", USER, "2026-11-11", updatedAt = 0))
        expectFailure<SQLiteConstraintException> {
            db.dailyActivityDao().insertDailyActivity(DailyActivityEntity("d-2", USER, "2026-11-11", updatedAt = 0))
        }
    }

    // ------------------------------------------------------------- delete behaviour

    @Test
    fun deletingProfile_cascadesToUserData_butKeepsContent() = runTest {
        insertSessionWithQuestion()
        db.sessionAnswerDao().insertSelectedOptions(listOf(SessionAnswerOptionEntity("o-1", "a-1", Content.Q_SINGLE_B)))
        db.openHelper.writableDatabase.execSQL("DELETE FROM user_profiles WHERE userId = '$USER'")

        assertNull(db.assessmentSessionDao().getSessionById("s-1"))
        assertNull(db.sessionAnswerDao().getAnswerEntity("sq-1"))
        assertEquals(4, db.questionDao().getTotalQuestionsBySubject(Content.SUBJECT).first())
    }

    @Test
    fun hardDeletingMockDefinition_setsSessionReferenceToNull() = runTest {
        db.mockDefinitionDao().insertMockDefinition(mock())
        db.assessmentSessionDao().createSession(session(mockId = "mock-1"))
        db.mockDefinitionDao().deleteMockDefinition("mock-1")
        assertNull(db.assessmentSessionDao().getSessionById("s-1")!!.mockDefinitionId)
    }

    @Test
    fun contentUsedByAttempts_cannotBeDeleted_unusedContentCan() = runTest {
        insertSessionWithQuestion()
        val usedQuestion = db.questionDao().getQuestionWithOptions(Content.Q_SINGLE).first()!!.question
        expectFailure<SQLiteConstraintException> { db.questionDao().deleteQuestion(usedQuestion) }

        val unusedQuestion = db.questionDao().getQuestionWithOptions(Content.Q_UNUSED).first()!!.question
        db.questionDao().deleteQuestion(unusedQuestion)
        assertNull(db.questionDao().getQuestionWithOptions(Content.Q_UNUSED).first())
    }

    @Test
    fun foreignKeysAreEnforced() {
        val cursor = db.openHelper.writableDatabase.query("PRAGMA foreign_keys")
        cursor.use { assertTrue(it.moveToFirst() && it.getInt(0) == 1) }
    }

    @Test
    fun profileUpsert_doesNotDeleteChildRows() = runTest {
        db.mockDefinitionDao().insertMockDefinition(mock())
        db.userProfileDao().insertOrUpdateProfile(UserProfileEntity(USER, "Renamed", null, null, 30, 0, 1))
        assertEquals(1, db.mockDefinitionDao().observeMockDefinitions(USER).first().size)
        assertEquals("Renamed", db.userProfileDao().getProfileById(USER)!!.displayName)
    }
}
