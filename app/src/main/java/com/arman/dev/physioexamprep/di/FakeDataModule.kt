package com.arman.dev.physioexamprep.di

import com.arman.dev.physioexamprep.core.data.fake.InMemorySessionResultStore
import com.arman.dev.physioexamprep.core.domain.model.SessionResultStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Shared in-memory stores used by the fake feature repositories. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FakeDataModule {
    @Binds
    abstract fun bindSessionResultStore(impl: InMemorySessionResultStore): SessionResultStore
}
