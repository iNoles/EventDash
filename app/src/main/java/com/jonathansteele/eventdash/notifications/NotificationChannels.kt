package com.jonathansteele.eventdash.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val EVENT_COUNTDOWN = "event_countdown"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EVENT_COUNTDOWN,
                "Event Countdowns",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts 1 day before events and holidays"
            }

            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}
