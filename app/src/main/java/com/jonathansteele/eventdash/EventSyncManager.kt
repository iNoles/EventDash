package com.jonathansteele.eventdash

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.jonathansteele.eventdash.data.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventSyncManager(context: Context) {

    private val dataClient: DataClient = Wearable.getDataClient(context)

    /**
     * Push events to Wear device using Data Layer without JSON
     */
    suspend fun pushEvents(events: List<Event>) = withContext(Dispatchers.IO) {
        if (events.isEmpty()) return@withContext

        val dataMapList = ArrayList<DataMap>()
        events.forEach { event ->
            val map = DataMap().apply {
                putInt("id", event.id)
                putString("title", event.title)
                putString("date", event.date.toString())
                putString("emoji", event.emoji ?: "")
            }
            dataMapList.add(map)
        }

        val request = PutDataMapRequest.create("/events").apply {
            dataMap.putDataMapArrayList("events", dataMapList)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request)
    }

}
