package com.arman.dev.physioexamprep.core.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arman.dev.physioexamprep.core.data.repository.AssessmentRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.BookmarkRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.MockDefinitionRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.ProgressRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.UserProfileRepositoryImpl
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.database.Content
import com.arman.dev.physioexamprep.core.database.FakeTimeProvider
import com.arman.dev.physioexamprep.core.database.USER
import com.arman.dev.physioexamprep.core.database.createInMemoryDatabase
import com.arman.dev.physioexamprep.core.database.entity.QuestionOptionEntity
import com.arman.dev.physioexamprep.core.database.expectFailure
import com.arman.dev.physioexamprep.core.database.seedContent
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionStatus
import com.arman.dev.physioexamprep.core.domain.model.SessionType
import com.arman.dev.physioexamprep.core.domain.model.SyncEntityType
import com.arman.dev.physioexamprep.core.domain.model.SyncOperation
import com.arman.dev.physioexamprep.core.domain.usecase.CompleteAssessmentUseCase
import com.arman.dev.physioexamprep.core.domain.usecase.CreateCustomMockUseCase
import com.arman.dev.physioexamprep.core.domain.usecase.SaveAnswerUseCase
import com.arman.dev.physioexamprep.core.domain.usecase.StartAssessmentUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** End-to-end tests of repositories + use cases against an in-memory Room database. */
@RunWith(AndroidJUnit4::class)
class UserDataRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var time: FakeTimeProvider
    private lateinit var profiles: UserProfileRepositoryImpl
    private lateinit var mocks: MockDefinitionRepositoryImpl
    private lateinit var assessments: AssessmentRepositoryImpl
    private lateinit var bookmarks: BookmarkRepositoryImpl
    private lateinit var progress: ProgressRepositoryImpl
    private lateinit var createCustomMock: CreateCustomMockUseCase
    private lateinit var startAssessment: StartAssessmentUseCase
    private lateinit var saveAnswer: SaveAnswerUseCase
    private lateinit var completeAssessment: CompleteAssessmentUseCase

    @Before
    fun setUp() = runTest {
        db = createInMemoryDatabase()
        db.seedContent()
        time = FakeTimeProvider()
        profiles = UserProfileRepositoryImpl(db, time)
        mocks = MockDefinitionRepositoryImpl(db, time)
        assessments = AssessmentRepositoryImpl(db, time)
        bookmarks = BookmarkRepositoryImpl(db, time)
        progress = ProgressRepositoryImpl(db)
        createCustomMock = CreateCustomMockUseCase(mocks)
        startAssessment = StartAssessmentUseCase(assessments, mocks)
        saveAnswer = SaveAnswerUseCase(assessments)
        completeAssessment = CompleteAssessmentUseCase(assessments)
        profiles.saveProfile(USER, "Aditi", "aditi@example.com", "AIIMS 2027")
    }

    @After
    fun tearDown() = db.close()

    private val today get() = time.localDate(time.now)

    /** Mock over all three questions: 2 marks each, 0.5 negative. */
    private suspend fun startMock(): String {
        val mockId = createCustomMock(
            userId = USER, title = "My mock", description = null, durationMinutes = 30,
            questions = listOf(Content.Q_SINGLE, Content.Q_MULTI, Content.Q_OTHER).map { QuestionSelection(it, 2.0, 0.5) }
        )
        return startAssessment.fromMockDefinition(USER, mockId)
    }

    private suspend fun sessionQuestionId(sessionId: String, questionId: Long): String =
        assessments.observeSession(sessionId).first()!!.questions.first { it.questionId == questionId }.sessionQuestionId

    private suspend fun selected(sessionId: String, questionId: Long) =
        assessments.observeSession(sessionId).first()!!.questions.first { it.questionId == questionId }.selectedOptionIds

    // ------------------------------------------------------------- profile

    @Test
    fun createAndUpdateProfile_keepsCreatedAt() = runTest {
        val created = profiles.getProfile(USER)!!
        assertEquals(20, created.dailyQuestionGoal)
        time.advanceSeconds(60)
        profiles.saveProfile(USER, "Aditi S", null, "AIIMS 2027", dailyQuestionGoal = 40)

        val updated = profiles.observeProfile(USER).first()!!
        assertEquals("Aditi S", updated.displayName)
        assertEquals(40, updated.dailyQuestionGoal)
        assertEquals(created.createdAt, updated.createdAt)
        assertTrue(updated.updatedAt > created.updatedAt)
        // INSERT is queued; the follow-up UPDATE is coalesced into it.
        val outbox = db.syncOutboxDao().getPendingOperations(USER, 10)
        assertEquals(listOf(SyncOperation.INSERT), outbox.map { it.operation })
    }

    // ------------------------------------------------------------- mock definitions

    @Test
    fun createCustomMock_savesQuestionsInOrder() = runTest {
        val mockId = createCustomMock(
            userId = USER, title = "  Upper limb  ", description = "desc", durationMinutes = 20,
            questions = listOf(QuestionSelection(Content.Q_OTHER), QuestionSelection(Content.Q_SINGLE, 4.0, 1.0))
        )
        val mock = mocks.getMockDefinition(mockId)!!
        assertEquals("Upper limb", mock.title)
        assertEquals(2, mock.totalQuestions)
        assertEquals(listOf(Content.Q_OTHER, Content.Q_SINGLE), mock.questions.map { it.questionId })
        assertEquals(4.0, mock.questions[1].marks, 0.0)
        assertEquals(1, mocks.observeMockDefinitions(USER).first().size)

        mocks.deleteMockDefinition(mockId)
        assertNull(mocks.getMockDefinition(mockId))
        assertTrue(mocks.observeMockDefinitions(USER).first().isEmpty())
    }

    @Test
    fun createCustomMock_rejectsDuplicateQuestions_andWritesNothing() = runTest {
        expectFailure<IllegalArgumentException> {
            createCustomMock(USER, "Dup", null, 10, listOf(QuestionSelection(Content.Q_SINGLE), QuestionSelection(Content.Q_SINGLE)))
        }
        assertTrue(mocks.observeMockDefinitions(USER).first().isEmpty())
    }

    @Test
    fun createMock_withUnknownQuestion_rollsBackWholeDefinition() = runTest {
        expectFailure<Exception> {
            createCustomMock(USER, "Bad", null, 10, listOf(QuestionSelection(Content.Q_SINGLE), QuestionSelection(999_999L)))
        }
        assertTrue(mocks.observeMockDefinitions(USER).first().isEmpty())
    }

    // ------------------------------------------------------------- sessions

    @Test
    fun startSessionFromMock_copiesFixedQuestionList() = runTest {
        val sessionId = startMock()
        val detail = assessments.observeSession(sessionId).first()!!
        assertEquals(SessionType.MOCK, detail.session.sessionType)
        assertEquals(SessionStatus.IN_PROGRESS, detail.session.status)
        assertEquals(30 * 60L, detail.session.durationSeconds)
        assertNotNull(detail.session.mockDefinitionId)
        assertEquals(listOf(0, 1, 2), detail.questions.map { it.position })
        assertEquals(listOf(Content.Q_SINGLE, Content.Q_MULTI, Content.Q_OTHER), detail.questions.map { it.questionId })
        assertTrue(detail.questions.all { it.answerStatus == AnswerStatus.UNANSWERED && it.marks == 2.0 })
        assertEquals(1, assessments.observeActiveSessions(USER).first().size)
    }

    @Test
    fun singleSelectAnswer_allowsOneOption_andReplacesPrevious() = runTest {
        val sessionId = startMock()
        val sq = sessionQuestionId(sessionId, Content.Q_SINGLE)

        assertNull(saveAnswer(sq, setOf(Content.Q_SINGLE_A), additionalTimeSeconds = 10)) // mocks give no feedback
        saveAnswer(sq, setOf(Content.Q_SINGLE_B), additionalTimeSeconds = 5)
        assertEquals(setOf(Content.Q_SINGLE_B), selected(sessionId, Content.Q_SINGLE))

        expectFailure<IllegalArgumentException> { saveAnswer(sq, setOf(Content.Q_SINGLE_A, Content.Q_SINGLE_C)) }
        val state = assessments.observeSession(sessionId).first()!!.questions.first { it.sessionQuestionId == sq }
        assertEquals(setOf(Content.Q_SINGLE_B), state.selectedOptionIds) // failed save changed nothing
        assertEquals(AnswerStatus.ANSWERED, state.answerStatus)
        assertEquals(15L, state.timeSpentSeconds)
        assertTrue(state.isVisited)
    }

    @Test
    fun multiSelectAnswer_storesAllOptions_andReplacesAtomically() = runTest {
        val sessionId = startMock()
        val sq = sessionQuestionId(sessionId, Content.Q_MULTI)

        saveAnswer(sq, setOf(Content.Q_MULTI_A, Content.Q_MULTI_C))
        assertEquals(setOf(Content.Q_MULTI_A, Content.Q_MULTI_C), selected(sessionId, Content.Q_MULTI))

        saveAnswer(sq, setOf(Content.Q_MULTI_A, Content.Q_MULTI_B))
        assertEquals(setOf(Content.Q_MULTI_A, Content.Q_MULTI_B), selected(sessionId, Content.Q_MULTI))

        // An option from another question is rejected and the previous selection survives.
        expectFailure<IllegalArgumentException> { saveAnswer(sq, setOf(Content.Q_MULTI_A, Content.Q_OTHER_A)) }
        assertEquals(setOf(Content.Q_MULTI_A, Content.Q_MULTI_B), selected(sessionId, Content.Q_MULTI))

        // Empty selection clears the answer.
        saveAnswer(sq, emptySet())
        val state = assessments.observeSession(sessionId).first()!!.questions.first { it.sessionQuestionId == sq }
        assertEquals(AnswerStatus.UNANSWERED, state.answerStatus)
        assertTrue(state.selectedOptionIds.isEmpty())
    }

    @Test
    fun reviewAndVisitedFlags_andQuestionIndex() = runTest {
        val sessionId = startMock()
        val sq = sessionQuestionId(sessionId, Content.Q_OTHER)
        assessments.setMarkedForReview(sq, true)
        assessments.markVisited(sq, additionalTimeSeconds = 7)
        assessments.updateCurrentQuestionIndex(sessionId, 2)

        val detail = assessments.observeSession(sessionId).first()!!
        val state = detail.questions.first { it.sessionQuestionId == sq }
        assertTrue(state.isMarkedForReview && state.isVisited)
        assertEquals(7L, state.timeSpentSeconds)
        assertEquals(2, detail.session.currentQuestionIndex)
    }

    // ------------------------------------------------------------- completion

    @Test
    fun completeMock_storesResultSnapshot_andUpdatesProgress() = runTest {
        val sessionId = startMock()
        saveAnswer(sessionQuestionId(sessionId, Content.Q_SINGLE), setOf(Content.Q_SINGLE_B), 20) // correct
        saveAnswer(sessionQuestionId(sessionId, Content.Q_MULTI), setOf(Content.Q_MULTI_A), 40)   // partial = wrong
        // Q_OTHER left unanswered
        time.advanceSeconds(90)

        val result = completeAssessment(sessionId)
        assertEquals(3, result.totalQuestions)
        assertEquals(2, result.attemptedQuestions)
        assertEquals(1, result.correctAnswers)
        assertEquals(1, result.incorrectAnswers)
        assertEquals(1, result.unattemptedQuestions)
        assertEquals(6.0, result.totalMarks, 0.0)
        assertEquals(1.5, result.obtainedMarks, 0.0) // +2 − 0.5
        assertEquals(50.0, result.accuracy, 0.0)
        assertEquals(90L, result.timeTakenSeconds)

        val session = assessments.observeSession(sessionId).first()!!.session
        assertEquals(SessionStatus.COMPLETED, session.status)
        assertEquals(result, assessments.observeResult(sessionId).first())
        assertEquals(1, assessments.observeCompletedAssessments(USER).first().size)
        assertTrue(assessments.observeActiveSessions(USER).first().isEmpty())

        // Progress recorded at completion for attempted questions only.
        assertEquals(1, progress.observeQuestionProgress(USER, Content.Q_SINGLE).first()!!.correctCount)
        assertEquals(1, progress.observeQuestionProgress(USER, Content.Q_MULTI).first()!!.incorrectCount)
        assertNull(progress.observeQuestionProgress(USER, Content.Q_OTHER).first())
        val chapter = progress.observeChapterProgress(USER, Content.CHAPTER_A).first()!!
        assertEquals(2, chapter.attemptedQuestions)
        assertEquals(1, chapter.correctQuestions)
        val day = progress.getActivityForDate(USER, today)!!
        assertEquals(2, day.questionsAttempted)
        assertEquals(60L, day.timeSpentSeconds)

        // Completing again returns the same stored result and changes nothing.
        assertEquals(result, completeAssessment(sessionId))
        assertEquals(1, progress.observeQuestionProgress(USER, Content.Q_SINGLE).first()!!.attemptCount)

        // Completed sessions are read-only.
        expectFailure<IllegalStateException> { saveAnswer(sessionQuestionId(sessionId, Content.Q_OTHER), setOf(Content.Q_OTHER_A)) }

        // Sync outbox has the session and result.
        val types = db.syncOutboxDao().getPendingOperations(USER, 100).map { it.entityType }.toSet()
        assertTrue(types.containsAll(setOf(SyncEntityType.ASSESSMENT_SESSION, SyncEntityType.SESSION_RESULT)))
    }

    @Test
    fun resultSnapshot_doesNotChangeWhenContentChanges() = runTest {
        val sessionId = startMock()
        saveAnswer(sessionQuestionId(sessionId, Content.Q_SINGLE), setOf(Content.Q_SINGLE_B))
        val before = completeAssessment(sessionId)

        // Content update: the correct answer of Q_SINGLE changes.
        db.questionOptionDao().upsertOptions(
            listOf(
                QuestionOptionEntity(Content.Q_SINGLE_A, Content.Q_SINGLE, "O", true, 1),
                QuestionOptionEntity(Content.Q_SINGLE_B, Content.Q_SINGLE, "O", false, 2)
            )
        )
        assertEquals(before, assessments.observeResult(sessionId).first())
    }

    @Test
    fun abandonedSession_cannotBeCompleted() = runTest {
        val sessionId = startMock()
        assessments.abandonSession(sessionId)
        assertEquals(SessionStatus.ABANDONED, assessments.observeSession(sessionId).first()!!.session.status)
        expectFailure<IllegalStateException> { completeAssessment(sessionId) }
        assertNull(assessments.observeResult(sessionId).first())
    }

    // ------------------------------------------------------------- practice + progress

    @Test
    fun practiceAnswer_isEvaluatedImmediately_lockedAndRecorded() = runTest {
        val sessionId = startAssessment(USER, SessionType.PRACTICE, "Practice", listOf(QuestionSelection(Content.Q_SINGLE)))
        val sq = sessionQuestionId(sessionId, Content.Q_SINGLE)

        val outcome = saveAnswer(sq, setOf(Content.Q_SINGLE_A), additionalTimeSeconds = 30)!!
        assertFalse(outcome.isCorrect)
        assertEquals(setOf(Content.Q_SINGLE_B), outcome.correctOptionIds)

        // Locked once checked.
        expectFailure<IllegalStateException> { saveAnswer(sq, setOf(Content.Q_SINGLE_B)) }

        val qp = progress.observeQuestionProgress(USER, Content.Q_SINGLE).first()!!
        assertEquals(1, qp.attemptCount)
        assertEquals(1, qp.incorrectCount)
        val day = progress.getActivityForDate(USER, today)!!
        assertEquals(1, day.questionsAttempted)
        assertEquals(30L, day.timeSpentSeconds)

        // Completing a practice session does not count the answer twice.
        val result = completeAssessment(sessionId)
        assertEquals(0, result.correctAnswers)
        assertEquals(1, progress.observeQuestionProgress(USER, Content.Q_SINGLE).first()!!.attemptCount)
        assertEquals(1, progress.getActivityForDate(USER, today)!!.questionsAttempted)
    }

    @Test
    fun questionAndChapterProgress_acrossAttempts() = runTest {
        suspend fun practise(option: Long) {
            val s = startAssessment(USER, SessionType.PRACTICE, "P", listOf(QuestionSelection(Content.Q_SINGLE)))
            saveAnswer(sessionQuestionId(s, Content.Q_SINGLE), setOf(option))
            completeAssessment(s)
        }
        practise(Content.Q_SINGLE_A) // wrong
        practise(Content.Q_SINGLE_B) // right
        practise(Content.Q_SINGLE_B) // right again

        val qp = progress.observeQuestionProgress(USER, Content.Q_SINGLE).first()!!
        assertEquals(3, qp.attemptCount)
        assertEquals(2, qp.correctCount)
        assertEquals(1, qp.incorrectCount)
        // Chapter counters are distinct questions, not attempts.
        val chapter = progress.observeChapterProgress(USER, Content.CHAPTER_A).first()!!
        assertEquals(1, chapter.attemptedQuestions)
        assertEquals(1, chapter.correctQuestions)
        assertEquals(1, progress.observeChapterProgressForSubject(USER, Content.SUBJECT).first().size)
    }

    @Test
    fun dailyActivity_accumulatesPerDay() = runTest {
        val s1 = startAssessment(USER, SessionType.PRACTICE, "P", listOf(QuestionSelection(Content.Q_SINGLE), QuestionSelection(Content.Q_OTHER)))
        saveAnswer(sessionQuestionId(s1, Content.Q_SINGLE), setOf(Content.Q_SINGLE_B), 10)
        saveAnswer(sessionQuestionId(s1, Content.Q_OTHER), setOf(Content.Q_OTHER_A), 20)
        val day1 = today

        time.advanceSeconds(24 * 60 * 60)
        val s2 = startAssessment(USER, SessionType.PRACTICE, "P", listOf(QuestionSelection(Content.Q_MULTI)))
        saveAnswer(sessionQuestionId(s2, Content.Q_MULTI), setOf(Content.Q_MULTI_A, Content.Q_MULTI_B), 5)

        val history = progress.observeActivityHistory(USER, fromDate = "2000-01-01").first()
        assertEquals(listOf(today, day1), history.map { it.activityDate })
        assertEquals(listOf(1, 2), history.map { it.questionsAttempted })
        assertEquals(listOf(5L, 30L), history.map { it.timeSpentSeconds })
    }

    // ------------------------------------------------------------- bookmarks

    @Test
    fun bookmarks_addRemoveRestore() = runTest {
        assertFalse(bookmarks.isBookmarked(USER, Content.Q_MULTI).first())
        bookmarks.addBookmark(USER, Content.Q_MULTI)
        bookmarks.addBookmark(USER, Content.Q_MULTI) // no-op
        assertTrue(bookmarks.isBookmarked(USER, Content.Q_MULTI).first())
        assertEquals(listOf(Content.Q_MULTI), bookmarks.observeBookmarkedQuestionIds(USER).first())
        val rowId = db.questionBookmarkDao().getBookmark(USER, Content.Q_MULTI)!!.id

        bookmarks.removeBookmark(USER, Content.Q_MULTI)
        assertFalse(bookmarks.isBookmarked(USER, Content.Q_MULTI).first())
        assertTrue(bookmarks.observeBookmarkedQuestionIds(USER).first().isEmpty())

        bookmarks.addBookmark(USER, Content.Q_MULTI) // restores the same row
        assertEquals(rowId, db.questionBookmarkDao().getBookmark(USER, Content.Q_MULTI)!!.id)
        assertEquals(listOf(Content.Q_MULTI), db.questionBookmarkDao().observeBookmarkedQuestions(USER).first().map { it.id })

        val ops = db.syncOutboxDao().getPendingOperations(USER, 100)
            .filter { it.entityType == SyncEntityType.QUESTION_BOOKMARK }.map { it.operation }
        assertEquals(listOf(SyncOperation.INSERT, SyncOperation.DELETE, SyncOperation.UPDATE), ops)
    }
}
