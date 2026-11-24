package com.arman.dev.physioexamprep.feature.quiz.data

import com.arman.dev.physioexamprep.core.domain.model.PracticeMode
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.domain.model.QuizSource
import com.arman.dev.physioexamprep.core.domain.model.ScoredSession
import com.arman.dev.physioexamprep.core.domain.model.SessionResultStore
import com.arman.dev.physioexamprep.core.domain.model.SubjectScore
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizQuestion
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizRepository
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizSession
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeQuizRepository @Inject constructor(
    private val resultStore: SessionResultStore,
) : QuizRepository {

    private val bookmarked = mutableSetOf<Long>()
    private val flagged = mutableSetOf<Long>()

    override suspend fun startSession(request: QuizRequest): QuizSession {
        val bank = FakeQuestionBank.questions
        // Topic matches first, then the rest of the subject, then anything (Quick mix / PYQ / mocks).
        val ordered: List<QuizQuestion> = when (request.source) {
            QuizSource.QUICK_MIX, QuizSource.MOCK -> bank.shuffled()
            else -> {
                val inTopics = bank.filter { it.topicId in request.topicIds }
                val inSubject = bank.filter { it.subjectId == request.subjectId && it !in inTopics }
                (inTopics + inSubject + (bank - inTopics.toSet() - inSubject.toSet())).distinct()
            }
        }
        val questions = ordered.take(request.questionCount.coerceAtLeast(1))
        val title = when (request.source) {
            QuizSource.QUICK_MIX -> "Quick ${request.questionCount}"
            QuizSource.PYQ -> "PYQ · ${request.refId.orEmpty()}"
            QuizSource.MOCK -> "Mock · ${request.refId.orEmpty()}"
            else -> questions.firstOrNull()?.subjectName ?: "Practice"
        }
        val limit = when (request.mode) {
            PracticeMode.PRACTICE -> null
            // ~0.8 min per question, same estimate as the topic picker.
            PracticeMode.TIMED, PracticeMode.EXAM -> questions.size * 48
        }
        return QuizSession(UUID.randomUUID().toString(), title, request.mode, questions, limit)
    }

    override suspend fun submit(session: QuizSession, answers: Map<Long, Long>, elapsedSeconds: Long): String {
        val bySubject = session.questions.groupBy { it.subjectId }.map { (subjectId, qs) ->
            SubjectScore(
                subjectId = subjectId,
                subjectName = qs.first().subjectName,
                correct = qs.count { answers[it.id] == it.correctOptionId },
                total = qs.size,
            )
        }
        resultStore.save(
            ScoredSession(
                id = session.id,
                title = session.title,
                totalQuestions = session.questions.size,
                attempted = session.questions.count { answers.containsKey(it.id) },
                correct = session.questions.count { answers[it.id] == it.correctOptionId },
                timeTakenSeconds = elapsedSeconds,
                subjects = bySubject,
            )
        )
        return session.id
    }

    override suspend fun setBookmarked(questionId: Long, bookmarked: Boolean) {
        if (bookmarked) this.bookmarked += questionId else this.bookmarked -= questionId
    }

    override suspend fun setFlagged(questionId: Long, flagged: Boolean) {
        if (flagged) this.flagged += questionId else this.flagged -= questionId
    }
}
