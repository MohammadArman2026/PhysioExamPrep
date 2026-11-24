package com.arman.dev.physioexamprep.feature.mockresult.di

import com.arman.dev.physioexamprep.feature.mockresult.data.FakeMockResultRepository
import com.arman.dev.physioexamprep.feature.mockresult.domain.MockResultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MockResultModule {
    @Binds
    abstract fun bindMockResultRepository(impl: FakeMockResultRepository): MockResultRepository
}
