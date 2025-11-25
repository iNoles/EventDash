package com.jonathansteele.eventdash

import android.content.Context
import com.jonathansteele.eventdash.data.HolidayJson
import kotlinx.serialization.json.Json
import javax.inject.Inject

class HolidayParser @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Read and decode holidays.json from assets.
     * Throws exception on decode errors (caller may handle).
     */
    fun loadFromAssets(context: Context, filename: String = "holidays.json"): List<HolidayJson> {
        val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
        return json.decodeFromString(jsonString)
    }
}
