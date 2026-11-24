package com.arman.dev.physioexamprep.feature.mocks.data

import com.arman.dev.physioexamprep.feature.mocks.domain.Difficulty
import com.arman.dev.physioexamprep.feature.mocks.domain.FullLengthMock
import com.arman.dev.physioexamprep.feature.mocks.domain.LiveMock
import com.arman.dev.physioexamprep.feature.mocks.domain.MockStatus
import com.arman.dev.physioexamprep.feature.mocks.domain.MocksOverview
import com.arman.dev.physioexamprep.feature.mocks.domain.MocksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Data from the Figma "07 · Mock tests" frame. The live mock starts 4d 12h 36m 8s after app start. */
@Singleton
class FakeMocksRepository @Inject constructor() : MocksRepository {

    private val startsAt = System.currentTimeMillis() + ((((4L * 24 + 12) * 60 + 36) * 60 + 8) * 1000)

    private val overview = MutableStateFlow(
        MocksOverview(
            liveMock = LiveMock("aim-4", "All‑India Mock #4", startsAt, joinedCount = 2340, isReserved = false, questionCount = 200, durationMinutes = 180),
            fullLength = listOf(
                FullLengthMock("mock-1", 1, "Foundations", Difficulty.EASY, 100, 120, MockStatus.Completed(72, 100, "mock-1")),
                FullLengthMock("mock-2", 2, "Core concepts", Difficulty.MEDIUM, 100, 120, MockStatus.Completed(64, 100, "mock-2")),
                FullLengthMock("mock-3", 3, "Clinical reasoning", Difficulty.HARD, 100, 120, MockStatus.Available),
                FullLengthMock("mock-4", 4, "Grand test", Difficulty.HARD, 100, 120, MockStatus.Locked),
            ),
        )
    )

    override fun observeOverview(): Flow<MocksOverview> = overview.asStateFlow()

    override suspend fun reserveSeat(liveMockId: String) {
        overview.update { o ->
            val live = o.liveMock ?: return@update o
            if (live.id != liveMockId || live.isReserved) o
            else o.copy(liveMock = live.copy(isReserved = true, joinedCount = live.joinedCount + 1))
        }
    }
}
