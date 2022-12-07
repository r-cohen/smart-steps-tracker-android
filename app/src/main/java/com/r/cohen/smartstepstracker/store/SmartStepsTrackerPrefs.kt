package com.r.cohen.smartstepstracker.store

import com.google.gson.Gson
import com.r.cohen.smartstepstracker.SmartStepsTrackerApp

object SmartStepsTrackerPrefs {
    private val prefs = SmartStepsTrackerApp.prefs
    private val gson = Gson()

    private const val logsKey = "logs"
    private const val stepsCountLastValueKey = "stepsCountLastValue"
    private const val stepsCountCumulativeDeltaKey = "stepsCountCumulativeDelta"
    private const val selectedThemeValueKey = "selectedThemeValue"

    fun clearAll() = prefs.edit().clear().apply()

    fun setLogs(logs: String) = prefs.edit().putString(logsKey, logs).apply()
    fun getLogs() = prefs.getString(logsKey, "") ?: ""
    fun clearLogs() = prefs.edit().remove(logsKey).apply()

    fun setStepsCountLastValue(value: Int) {
        val last = StepsCountMeasure(System.currentTimeMillis(), value)
        prefs.edit().putString(stepsCountLastValueKey, gson.toJson(last)).apply()
    }
    fun getStepsCountLastValue(): StepsCountMeasure? {
        val json = prefs.getString(stepsCountLastValueKey, "") ?: ""
        if (json.isNotEmpty()) {
            return gson.fromJson(json, StepsCountMeasure::class.java)
        }
        return null
    }
    fun clearStepsCountLastValue() = prefs.edit().remove(stepsCountLastValueKey).apply()

    fun setStepsCountCumulativeDelta(deltaValue: Int) {
        val delta = StepsCountMeasure(System.currentTimeMillis(), deltaValue)
        prefs.edit().putString(stepsCountCumulativeDeltaKey, gson.toJson(delta)).apply()
    }
    fun getStepsCountCumulativeDelta(): StepsCountMeasure? {
        val json = prefs.getString(stepsCountCumulativeDeltaKey, "") ?: ""
        if (json.isNotEmpty()) {
            return gson.fromJson(json, StepsCountMeasure::class.java)
        }
        return null
    }

    fun setSelectedThemeValue(value: String) = prefs.edit().putString(selectedThemeValueKey, value).apply()
    fun getSelectedThemeValue(): String = prefs.getString(selectedThemeValueKey, "0") ?: "0"
}