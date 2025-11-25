package com.jonathansteele.eventdash.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jonathansteele.eventdash.data.EventRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: EventRepository

    @Inject
    lateinit var notifier: CountdownNotifier

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.getAllEventsFlow().collect { list ->
                    list.forEach { event ->
                        if (!event.isHoliday) {
                            notifier.scheduleNotification(
                                event.id,
                                event.title,
                                event.date
                            )
                        }
                    }
                }
            }
        }
    }
}
