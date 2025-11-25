package com.jonathansteele.eventdash.data

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun fromString(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun toString(date: LocalDate?): String? = date?.toString()
}
