package com.arman.dev.physioexamprep.feature.mocks.di

import com.arman.dev.physioexamprep.feature.mocks.data.FakeMocksRepository
import com.arman.dev.physioexamprep.feature.mocks.domain.MocksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MocksModule {
    @Binds
    abstract fun bindMocksRepository(impl: FakeMocksRepository): MocksRepository
}
