package com.arman.dev.physioexamprep.di

import android.content.Context
import androidx.room.Room
import com.arman.dev.physioexamprep.core.database.AppDatabase
import com.arman.dev.physioexamprep.core.database.dao.ChapterDao
import com.arman.dev.physioexamprep.core.database.dao.QuestionDao
import com.arman.dev.physioexamprep.core.database.dao.QuestionOptionDao
import com.arman.dev.physioexamprep.core.database.dao.SubjectDao
import com.arman.dev.physioexamprep.core.database.migration.ALL_MIGRATIONS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Explicit migrations only – no destructive fallback (user progress lives here).
            .addMigrations(*ALL_MIGRATIONS)
            .build()

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideChapterDao(database: AppDatabase): ChapterDao = database.chapterDao()

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideQuestionOptionDao(database: AppDatabase): QuestionOptionDao = database.questionOptionDao()
}
