package com.jonathansteele.eventdash.di

import android.app.Application
import androidx.room.Room
import com.jonathansteele.eventdash.data.EventDao
import com.jonathansteele.eventdash.data.EventDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): EventDatabase =
        Room.databaseBuilder(app, EventDatabase::class.java, "events.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideEventDao(db: EventDatabase): EventDao = db.eventDao()
}