package com.arman.dev.physioexamprep.core.data.mapper

import com.arman.dev.physioexamprep.core.database.entity.user.AssessmentSessionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.ChapterProgressEntity
import com.arman.dev.physioexamprep.core.database.entity.user.DailyActivityEntity
import com.arman.dev.physioexamprep.core.database.entity.user.MockDefinitionEntity
import com.arman.dev.physioexamprep.core.database.entity.user.QuestionProgressEntity
import com.arman.dev.physioexamprep.core.database.entity.user.SessionResultEntity
import com.arman.dev.physioexamprep.core.database.entity.user.UserProfileEntity
import com.arman.dev.physioexamprep.core.database.relation.user.AssessmentSessionWithQuestions
import com.arman.dev.physioexamprep.core.database.relation.user.MockDefinitionWithQuestions
import com.arman.dev.physioexamprep.core.database.relation.user.SessionQuestionWithAnswer
import com.arman.dev.physioexamprep.core.domain.model.AnswerStatus
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSession
import com.arman.dev.physioexamprep.core.domain.model.AssessmentSessionDetail
import com.arman.dev.physioexamprep.core.domain.model.ChapterProgress
import com.arman.dev.physioexamprep.core.domain.model.DailyActivity
import com.arman.dev.physioexamprep.core.domain.model.MockDefinition
import com.arman.dev.physioexamprep.core.domain.model.QuestionProgress
import com.arman.dev.physioexamprep.core.domain.model.QuestionSelection
import com.arman.dev.physioexamprep.core.domain.model.SessionQuestionState
import com.arman.dev.physioexamprep.core.domain.model.SessionResult
import com.arman.dev.physioexamprep.core.domain.model.UserProfile

fun UserProfileEntity.toDomain() = UserProfile(
    userId = userId,
    displayName = displayName,
    email = email,
    targetExam = targetExam,
    dailyQuestionGoal = dailyQuestionGoal,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MockDefinitionEntity.toDomain(questions: List<QuestionSelection> = emptyList()) = MockDefinition(
    id = id,
    userId = userId,
    title = title,
    description = description,
    mockType = mockType,
    sourcePaperId = sourcePaperId,
    durationMinutes = durationMinutes,
    totalQuestions = totalQuestions,
    filterConfig = filterConfig,
    createdAt = createdAt,
    updatedAt = updatedAt,
    questions = questions
)

fun MockDefinitionWithQuestions.toDomain() = mockDefinition.toDomain(
    questions = sortedQuestions.map { QuestionSelection(it.questionId, it.marks, it.negativeMarks) }
)

fun AssessmentSessionEntity.toDomain() = AssessmentSession(
    id = id,
    userId = userId,
    sessionType = sessionType,
    title = title,
    mockDefinitionId = mockDefinitionId,
    sourcePaperId = sourcePaperId,
    durationSeconds = durationSeconds,
    startedAt = startedAt,
    completedAt = completedAt,
    currentQuestionIndex = currentQuestionIndex,
    status = status
)

fun SessionQuestionWithAnswer.toDomain(): SessionQuestionState {
    val answerEntity = answer?.answer
    return SessionQuestionState(
        sessionQuestionId = sessionQuestion.id,
        questionId = sessionQuestion.questionId,
        position = sessionQuestion.position,
        marks = sessionQuestion.marks,
        negativeMarks = sessionQuestion.negativeMarks,
        answerStatus = answerEntity?.answerStatus ?: AnswerStatus.UNANSWERED,
        selectedOptionIds = answer?.selectedOptionIds.orEmpty(),
        isMarkedForReview = answerEntity?.isMarkedForReview ?: false,
        isVisited = answerEntity?.isVisited ?: false,
        timeSpentSeconds = answerEntity?.timeSpentSeconds ?: 0,
        answeredAt = answerEntity?.answeredAt
    )
}

fun AssessmentSessionWithQuestions.toDomain() = AssessmentSessionDetail(
    session = session.toDomain(),
    questions = sortedQuestions.map { it.toDomain() }
)

fun SessionResultEntity.toDomain() = SessionResult(
    sessionId = sessionId,
    totalQuestions = totalQuestions,
    attemptedQuestions = attemptedQuestions,
    correctAnswers = correctAnswers,
    incorrectAnswers = incorrectAnswers,
    unattemptedQuestions = unattemptedQuestions,
    totalMarks = totalMarks,
    obtainedMarks = obtainedMarks,
    accuracy = accuracy,
    timeTakenSeconds = timeTakenSeconds,
    completedAt = completedAt
)

fun QuestionProgressEntity.toDomain() = QuestionProgress(
    userId = userId,
    questionId = questionId,
    attemptCount = attemptCount,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    lastAttemptAt = lastAttemptAt
)

fun ChapterProgressEntity.toDomain() = ChapterProgress(
    userId = userId,
    chapterId = chapterId,
    attemptedQuestions = attemptedQuestions,
    correctQuestions = correctQuestions,
    lastPracticedAt = lastPracticedAt
)

fun DailyActivityEntity.toDomain() = DailyActivity(
    userId = userId,
    activityDate = activityDate,
    questionsAttempted = questionsAttempted,
    timeSpentSeconds = timeSpentSeconds
)
