package com.jonathansteele.eventdash.di

import android.app.Application
import com.jonathansteele.eventdash.HolidayCalculator
import com.jonathansteele.eventdash.HolidayParser
import com.jonathansteele.eventdash.data.EventDao
import com.jonathansteele.eventdash.data.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideHolidayParser(): HolidayParser = HolidayParser()

    @Provides
    @Singleton
    fun provideHolidayCalculator(): HolidayCalculator = HolidayCalculator()

    @Provides
    @Singleton
    fun provideEventRepository(
        app: Application,
        dao: EventDao,
        parser: HolidayParser,
        calc: HolidayCalculator
    ): EventRepository = EventRepository(app, dao, parser, calc)
}