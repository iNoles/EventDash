package com.jonathansteele.eventdash.data

import android.content.Context
import com.jonathansteele.eventdash.HolidayCalculator
import com.jonathansteele.eventdash.HolidayParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val context: Context,
    private val dao: EventDao,
    private val holidayParser: HolidayParser,
    private val holidayCalculator: HolidayCalculator
) {

    private val predefinedHolidays: List<Event> by lazy {
        loadHolidays()
    }

    private fun loadHolidays(): List<Event> {
        val list: List<HolidayJson> = holidayParser.loadFromAssets(context)
        return list.map { h ->
            val date = if (h.month != null && h.day != null) {
                // normalize fixed date (this year or next)
                holidayCalculator.computeNextDate(h)
            } else if (!h.note.isNullOrBlank()) {
                holidayCalculator.computeNextDate(h)
            } else {
                throw IllegalArgumentException("Holiday must have month/day or note: $h")
            }

            Event(
                id = 0, // not a DB entry
                title = h.name,
                date = date,
                isHoliday = true,
                emoji = h.emoji
            )
        }
    }

    /**
     * Combined Flow: yields holidays (predefined) + user events (db).
     * Holidays come first; user events appended; you can sort in the consumer if desired.
     */
    fun getAllEventsFlow(): Flow<List<Event>> {
        val holidaysFlow = flowOf(predefinedHolidays)
        val usersFlow = dao.getAllEvents().map { list ->
            list.map { e -> Event(e.id, e.title, e.date, e.isHoliday, e.emoji) }
        }

        return combine(holidaysFlow, usersFlow) { holidays, users ->
            // Merge and sort by date ascending
            (holidays + users).sortedBy { it.date }
        }
    }

    suspend fun addUserEvent(title: String, date: LocalDate, emoji: String? = null) {
        dao.insert(EventEntity(title = title, date = date, isHoliday = false, emoji = emoji))
    }

    suspend fun deleteUserEvent(entity: EventEntity) {
        dao.delete(entity)
    }
}
