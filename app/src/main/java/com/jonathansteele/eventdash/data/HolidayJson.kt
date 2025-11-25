package com.jonathansteele.eventdash.data

import kotlinx.serialization.Serializable

/**
 * JSON items in assets/holidays.json.
 *
 * - month/day: used for fixed-date holidays (e.g. 12/25)
 * - note: optional text describing rules (e.g. "Third Monday in January", "Last Monday in May",
 *         "Fourth Thursday in November"). HolidayCalculator parses common patterns.
 * - emoji: optional emoji/icon to show in UI
 */
@Serializable
data class HolidayJson(
    val name: String,
    val month: Int? = null,    // 1..12 (for fixed dates)
    val day: Int? = null,      // 1..31 (for fixed dates)
    val note: String? = null,  // optional natural-language rule for weekday-based holidays
    val emoji: String? = null
)
