package com.arman.dev.physioexamprep.feature.home.domain

import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeDashboard(): Flow<HomeDashboard>
    suspend fun registerForMock(mockId: String)
}
