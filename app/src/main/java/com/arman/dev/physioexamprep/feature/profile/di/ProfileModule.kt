package com.arman.dev.physioexamprep.feature.profile.di

import com.arman.dev.physioexamprep.feature.profile.data.FakeProfileRepository
import com.arman.dev.physioexamprep.feature.profile.domain.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    abstract fun bindProfileRepository(impl: FakeProfileRepository): ProfileRepository
}
