package com.jonathansteele.eventdash.di

import android.app.Application
import android.app.NotificationManager
import com.jonathansteele.eventdash.notifications.CountdownNotifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationManager(app: Application): NotificationManager =
        app.getSystemService(NotificationManager::class.java)

    @Provides
    @Singleton
    fun provideCountdownNotifier(app: Application): CountdownNotifier =
        CountdownNotifier(app)
}
