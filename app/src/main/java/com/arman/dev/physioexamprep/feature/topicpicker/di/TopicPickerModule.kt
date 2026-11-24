package com.arman.dev.physioexamprep.feature.topicpicker.di

import com.arman.dev.physioexamprep.feature.topicpicker.data.FakeTopicPickerRepository
import com.arman.dev.physioexamprep.feature.topicpicker.domain.TopicPickerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TopicPickerModule {
    @Binds
    abstract fun bindTopicPickerRepository(impl: FakeTopicPickerRepository): TopicPickerRepository
}
