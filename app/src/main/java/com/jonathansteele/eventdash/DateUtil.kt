package com.jonathansteele.eventdash

import androidx.compose.material3.DatePickerState
import java.time.LocalDate

fun DatePickerState.getLocalDateSafely(): LocalDate? {
    val millis = selectedDateMillis ?: return null
    val epochDay = millis / 86_400_000L
    return LocalDate.ofEpochDay(epochDay)
}