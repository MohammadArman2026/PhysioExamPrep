package com.arman.dev.physioexamprep.di

import com.arman.dev.physioexamprep.core.common.SystemTimeProvider
import com.arman.dev.physioexamprep.core.common.TimeProvider
import com.arman.dev.physioexamprep.core.data.repository.AssessmentRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.BookmarkRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.MockDefinitionRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.ProgressRepositoryImpl
import com.arman.dev.physioexamprep.core.data.repository.UserProfileRepositoryImpl
import com.arman.dev.physioexamprep.core.domain.repository.AssessmentRepository
import com.arman.dev.physioexamprep.core.domain.repository.BookmarkRepository
import com.arman.dev.physioexamprep.core.domain.repository.MockDefinitionRepository
import com.arman.dev.physioexamprep.core.domain.repository.ProgressRepository
import com.arman.dev.physioexamprep.core.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindMockDefinitionRepository(impl: MockDefinitionRepositoryImpl): MockDefinitionRepository

    @Binds
    @Singleton
    abstract fun bindAssessmentRepository(impl: AssessmentRepositoryImpl): AssessmentRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository
}
