package com.arman.dev.physioexamprep.feature.learn.di

import com.arman.dev.physioexamprep.feature.learn.data.FakeLearnRepository
import com.arman.dev.physioexamprep.feature.learn.domain.LearnRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LearnModule {
    @Binds
    abstract fun bindLearnRepository(impl: FakeLearnRepository): LearnRepository
}
