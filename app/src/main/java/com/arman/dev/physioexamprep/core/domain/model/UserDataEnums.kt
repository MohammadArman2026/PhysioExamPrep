package com.arman.dev.physioexamprep.core.domain.model

/*
 * Type-safe values for user-data status/type columns.
 * Room stores enums as TEXT using their [name], so never rename a constant
 * without a database migration.
 */

enum class MockType { CUSTOM, PREVIOUS_YEAR }

enum class SessionType {
    PRACTICE,
    TIMED_PRACTICE,
    PYQ_PRACTICE,
    MOCK;

    /**
     * Practice-style sessions check each answer immediately (answer is then locked
     * and progress is recorded right away). Mocks are evaluated only on submit.
     */
    val evaluatesImmediately: Boolean
        get() = this != MOCK
}

enum class SessionStatus { IN_PROGRESS, COMPLETED, ABANDONED }

enum class AnswerStatus { UNANSWERED, ANSWERED }

enum class SyncOperation { INSERT, UPDATE, DELETE }

/** Which user table a [SyncOperation] in the outbox refers to. */
enum class SyncEntityType {
    USER_PROFILE,
    MOCK_DEFINITION,
    ASSESSMENT_SESSION,
    SESSION_RESULT,
    QUESTION_BOOKMARK,
    QUESTION_PROGRESS,
    CHAPTER_PROGRESS,
    DAILY_ACTIVITY
}
