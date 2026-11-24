package com.arman.dev.physioexamprep.feature.home.di

import com.arman.dev.physioexamprep.feature.home.data.FakeHomeRepository
import com.arman.dev.physioexamprep.feature.home.domain.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    @Binds
    abstract fun bindHomeRepository(impl: FakeHomeRepository): HomeRepository
}
