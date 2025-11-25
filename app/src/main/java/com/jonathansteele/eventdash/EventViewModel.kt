package com.jonathansteele.eventdash

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathansteele.eventdash.data.Event
import com.jonathansteele.eventdash.data.EventEntity
import com.jonathansteele.eventdash.data.EventRepository
import com.jonathansteele.eventdash.notifications.CountdownNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val notifier: CountdownNotifier,
    @ApplicationContext context: Context
) : ViewModel() {

    /**
     * UI state for Compose screens
     */
    @Stable
    data class UiState(
        val events: List<Event> = emptyList(),
        val holidays: List<Event> = emptyList(),
        val userEvents: List<Event> = emptyList(),
        val loading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val syncManager by lazy { EventSyncManager(context) }

    init {
        observeEvents()
    }

    /**
     * Observe combined events (holidays + user events)
     */
    private fun observeEvents() {
        viewModelScope.launch {
            repository.getAllEventsFlow()
                .onStart { _uiState.update { it.copy(loading = true) } }
                .collect { list ->
                    val holidays = list.filter { it.isHoliday }
                    val user = list.filter { !it.isHoliday }

                    _uiState.value = UiState(
                        events = list,
                        holidays = holidays,
                        userEvents = user,
                        loading = false
                    )

                    // Auto schedule notifications for user events only
                    user.forEach { event ->
                        notifier.scheduleNotification(
                            event.id,
                            event.title,
                            event.date
                        )
                    }

                    // Reactive sync to Wear
                    syncToWear(list)
                }
        }
    }

    /**
     * Add a user event.
     */
    fun addEvent(title: String, date: LocalDate, emoji: String? = null) {
        viewModelScope.launch {
            repository.addUserEvent(title, date, emoji)
        }
    }

    /**
     * Delete a user event.
     */
    fun deleteEvent(event: Event) {
        if (event.isHoliday) return // cannot delete predefined holidays

        viewModelScope.launch {
            // cancel notification if exists
            notifier.cancelNotification(event.id)

            repository.deleteUserEvent(
                entity = EventEntity(
                    id = event.id,
                    title = event.title,
                    date = event.date,
                    isHoliday = false,
                    emoji = event.emoji
                )
            )
        }
    }

    /**
     * Push events to Wear device safely.
     */
    private fun syncToWear(events: List<Event>) {
        viewModelScope.launch {
            try {
                // Only send user events if needed
                val userEvents = events.filter { !it.isHoliday }
                if (userEvents.isNotEmpty()) {
                    syncManager.pushEvents(userEvents)
                }
            } catch (e: Exception) {
                // Log the error, but don’t crash
                e.printStackTrace()
            }
        }
    }
}
