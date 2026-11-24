package com.arman.dev.physioexamprep.feature.mocks.domain

import kotlinx.coroutines.flow.Flow

data class LiveMock(
    val id: String,
    val title: String,
    /** Epoch millis; the countdown is computed from this. */
    val startsAtMillis: Long,
    val joinedCount: Int,
    val isReserved: Boolean,
    val questionCount: Int,
    val durationMinutes: Int,
)

data class FullLengthMock(
    val id: String,
    val number: Int,
    val title: String,
    val difficulty: Difficulty,
    val questionCount: Int,
    val durationMinutes: Int,
    val status: MockStatus,
)

enum class Difficulty(val label: String) { EASY("Easy"), MEDIUM("Medium"), HARD("Hard") }

sealed interface MockStatus {
    data class Completed(val score: Int, val maxScore: Int, val resultId: String) : MockStatus
    data object Available : MockStatus
    /** Unlocks after the previous mock (guess — Figma only shows a lock icon). */
    data object Locked : MockStatus
}

data class MocksOverview(val liveMock: LiveMock?, val fullLength: List<FullLengthMock>)

interface MocksRepository {
    fun observeOverview(): Flow<MocksOverview>
    suspend fun reserveSeat(liveMockId: String)
}
