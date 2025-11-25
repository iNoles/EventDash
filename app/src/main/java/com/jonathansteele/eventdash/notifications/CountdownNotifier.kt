package com.jonathansteele.eventdash.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class CountdownNotifier @Inject constructor(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule a notification to fire exactly 1 day before the event date.
     * If the event is tomorrow, it fires today.
     * If the event is today or in the past, no notification is scheduled.
     */
    fun scheduleNotification(eventId: Int, title: String, date: LocalDate) {
        val triggerDate = date.minusDays(1)

        // If today or past → don't schedule
        if (!triggerDate.isAfter(LocalDate.now())) return

        val triggerDateTime = LocalDateTime.of(
            triggerDate.year,
            triggerDate.month,
            triggerDate.dayOfMonth,
            9, 0 // fire at 9:00 AM local time
        )

        val triggerMillis = triggerDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, CountdownReceiver::class.java).apply {
            putExtra("eventId", eventId)
            putExtra("title", title)
            putExtra("eventDate", date.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }

    fun cancelNotification(eventId: Int) {
        val intent = Intent(context, CountdownReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
