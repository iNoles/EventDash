package com.jonathansteele.eventdash

import com.jonathansteele.eventdash.data.HolidayJson
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.math.max

/**
 * Compute the next occurrence for a holiday defined by HolidayJson.
 *
 * Supports:
 *  - Fixed dates via month/day.
 *  - Common rules in the `note` string:
 *      * "First Monday in September"
 *      * "Third Monday in January"
 *      * "Last Monday in May"
 *      * "Second Monday in October"
 *      * "Fourth Thursday in November"
 *
 * If the computed date for the current year is before today, returns the date in next year.
 */
class HolidayCalculator @Inject constructor() {

    fun computeNextDate(h: HolidayJson): LocalDate {
        val now = LocalDate.now()
        val year = now.year

        val candidateThisYear: LocalDate = when {
            // Fixed date provided
            h.month != null && h.day != null -> {
                safeLocalDate(year, h.month, h.day)
            }

            // Try parse note rule
            !h.note.isNullOrBlank() -> parseRuleToDate(year, h.note)

            else -> throw IllegalArgumentException("HolidayJson must provide month/day or note: $h")
        }

        return if (!candidateThisYear.isBefore(now)) candidateThisYear else shiftToNextYear(
            candidateThisYear,
            h
        )
    }

    private fun shiftToNextYear(date: LocalDate, h: HolidayJson): LocalDate {
        // if fixed date, shift by 1 year preserving month/day
        return if (h.month != null && h.day != null) {
            safeLocalDate(date.year + 1, h.month, h.day)
        } else {
            // recompute rule for next year
            parseRuleToDate(date.year + 1, h.note ?: throw IllegalStateException("no rule"))
        }
    }

    private fun safeLocalDate(year: Int, month: Int, day: Int): LocalDate {
        // Some dates like Feb 29 may not exist in non-leap years; adjust by clamping day to last valid day.
        val m = Month.of(month)
        val maxDay = m.length(Year.isLeap(year.toLong()))
        val d = max(1, minOf(day, maxDay))
        return LocalDate.of(year, month, d)
    }

    private fun parseRuleToDate(year: Int, note: String): LocalDate {
        // Normalize string for simple parsing
        val n = note.trim().lowercase()

        // Determine target DayOfWeek (search for monday, tuesday, ...)
        val dow = when {
            "monday" in n -> DayOfWeek.MONDAY
            "tuesday" in n -> DayOfWeek.TUESDAY
            "wednesday" in n -> DayOfWeek.WEDNESDAY
            "thursday" in n -> DayOfWeek.THURSDAY
            "friday" in n -> DayOfWeek.FRIDAY
            "saturday" in n -> DayOfWeek.SATURDAY
            "sunday" in n -> DayOfWeek.SUNDAY
            else -> null
        }

        // Determine month if present (search for month names)
        Month.values().firstOrNull {
            it.name.lowercase().startsWith(
                // shortened matching: look for e.g. "january", "jan"
                it.name.lowercase().substring(0, 3)
            ) && it.name.lowercase() in n || it.name.lowercase() in n
        } ?: Month.values().firstOrNull {
            // fallback: check full name
            n.contains(it.name.lowercase())
        }

        // Simpler approach: test each month for presence
        val foundMonth = Month.values().firstOrNull { n.contains(it.name.lowercase()) }
        val targetMonth =
            foundMonth ?: throw IllegalArgumentException("Could not parse month from note: $note")

        // Detect ordinal: first, second, third, fourth, last
        return when {
            "last" in n -> {
                LocalDate.of(year, targetMonth.value, 1)
                    .with(TemporalAdjusters.lastInMonth(dow ?: DayOfWeek.MONDAY))
            }

            "first" in n -> {
                LocalDate.of(year, targetMonth.value, 1)
                    .with(TemporalAdjusters.firstInMonth(dow ?: DayOfWeek.MONDAY))
            }

            "second" in n -> {
                LocalDate.of(year, targetMonth.value, 1)
                    .with(TemporalAdjusters.dayOfWeekInMonth(2, dow ?: DayOfWeek.MONDAY))
            }

            "third" in n -> {
                LocalDate.of(year, targetMonth.value, 1)
                    .with(TemporalAdjusters.dayOfWeekInMonth(3, dow ?: DayOfWeek.MONDAY))
            }

            "fourth" in n -> {
                LocalDate.of(year, targetMonth.value, 1)
                    .with(TemporalAdjusters.dayOfWeekInMonth(4, dow ?: DayOfWeek.MONDAY))
            }

            else -> throw IllegalArgumentException("Unsupported rule format: $note")
        }
    }
}
