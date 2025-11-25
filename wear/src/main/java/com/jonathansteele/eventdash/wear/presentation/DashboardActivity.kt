/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.jonathansteele.eventdash.wear.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.jonathansteele.eventdash.wear.presentation.theme.EventDashTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EventDashTheme {
                val eventList by events.collectAsState()
                WearDashboardScreen(events = eventList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        Wearable.getDataClient(this).removeListener(this)
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // Yeah, baby, we’re swinging through these data events, shagadelic style
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val mojoItem: DataItem = event.dataItem // Our groovy little data item
                if (mojoItem.uri.path == "/events") {
                    // Get the DataMap like a true international man of mystery
                    val groovyDataMap = DataMapItem.fromDataItem(mojoItem).dataMap
                    val numberOfEvents = groovyDataMap.getInt("size") // How many groovy happenings?

                    val eventList = mutableListOf<Event>()
                    for (i in 0 until numberOfEvents) {
                        val id = groovyDataMap.getInt("event_${i}_id")
                        val title = groovyDataMap.getString("event_${i}_title") ?: ""
                        val dateStr = groovyDataMap.getString("event_${i}_date") ?: ""
                        val emoji = groovyDataMap.getString("event_${i}_emoji")
                        val isHoliday = groovyDataMap.getBoolean("event_${i}_isHoliday")
                        val date = LocalDate.parse(dateStr)

                        // Add each happening to our list, yeah baby
                        eventList.add(Event(id, title, date, isHoliday, emoji))
                    }

                    // Launch the mojo update in the lifecycle scope, groovy-style
                    lifecycleScope.launch {
                        _events.value = eventList
                    }
                }
            }
        }
    }

    @Composable
    fun WearDashboardScreen(events: List<Event>) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(events) { event ->
                WearEventCard(event)
            }
        }
    }

    @Composable
    fun WearEventCard(event: Event, modifier: Modifier = Modifier) {
        // Wrap card width to screen with padding
        Card(
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth(0.85f), // 85% of width fits almost all round screens
            shape = MaterialTheme.shapes.medium,
            onClick = {}
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.title2,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${event.daysLeft} days left",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }

    @Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        WearDashboardScreen(
            listOf(
                Event(
                    id = 1,
                    title = "Christmas",
                    date = LocalDate.of(2025, 12, 25),
                    isHoliday = true,
                    emoji = "🎄"
                ),
                Event(
                    id = 2,
                    title = "New Year",
                    date = LocalDate.of(2026, 1, 1),
                    isHoliday = true,
                    emoji = "🎉"
                ),
                Event(
                    id = 3,
                    title = "Birthday",
                    date = LocalDate.of(2026, 1, 9),
                    isHoliday = false,
                    emoji = "🎂"
                ),
                Event(
                    id = 4,
                    title = "Project Deadline",
                    date = LocalDate.of(2025, 11, 30),
                    isHoliday = false,
                    emoji = "💻"
                )
            )
        )
    }
}