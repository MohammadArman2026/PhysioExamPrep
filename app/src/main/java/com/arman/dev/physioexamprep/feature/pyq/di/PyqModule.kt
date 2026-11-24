package com.arman.dev.physioexamprep.feature.pyq.di

import com.arman.dev.physioexamprep.feature.pyq.data.FakePyqRepository
import com.arman.dev.physioexamprep.feature.pyq.domain.PyqRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PyqModule {
    @Binds
    abstract fun bindPyqRepository(impl: FakePyqRepository): PyqRepository
}
