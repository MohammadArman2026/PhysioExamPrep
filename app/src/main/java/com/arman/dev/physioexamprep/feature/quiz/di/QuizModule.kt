package com.arman.dev.physioexamprep.feature.quiz.di

import com.arman.dev.physioexamprep.feature.quiz.data.FakeQuizRepository
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizModule {
    @Binds
    abstract fun bindQuizRepository(impl: FakeQuizRepository): QuizRepository
}
