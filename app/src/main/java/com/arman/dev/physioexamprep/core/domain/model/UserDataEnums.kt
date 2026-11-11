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
