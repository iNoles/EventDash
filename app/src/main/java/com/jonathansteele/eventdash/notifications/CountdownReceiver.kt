package com.jonathansteele.eventdash.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CountdownReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getIntExtra("eventId", 0)
        val title = intent.getStringExtra("title") ?: "Upcoming Event"
        val dateString = intent.getStringExtra("eventDate") ?: ""

        val notification = NotificationCompat.Builder(context, NotificationChannels.EVENT_COUNTDOWN)
            //.setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$title is Tomorrow!")
            .setContentText("Happening on $dateString")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(eventId, notification)
    }
}
