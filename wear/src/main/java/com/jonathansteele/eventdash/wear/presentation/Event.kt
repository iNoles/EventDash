package com.jonathansteele.eventdash.wear.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Immutable
data class Event(
    val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val isHoliday: Boolean = false,
    val emoji: String? = null
) {
    val daysLeft: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), date)

    val urgency: Urgency
        get() = when {
            daysLeft <= 0 -> Urgency.TODAY_OR_PASSED
            daysLeft <= 3 -> Urgency.URGENT
            daysLeft <= 10 -> Urgency.SOON
            else -> Urgency.FUTURE
        }
}

enum class Urgency { TODAY_OR_PASSED, URGENT, SOON, FUTURE }
