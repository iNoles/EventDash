package com.jonathansteele.eventdash.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val isHoliday: Boolean = false,
    val emoji: String? = null
)
