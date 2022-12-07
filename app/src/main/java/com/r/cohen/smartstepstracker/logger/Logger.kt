package com.r.cohen.smartstepstracker.logger

import android.util.Log
import com.google.gson.Gson
import com.r.cohen.smartstepstracker.BuildConfig
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs
import java.text.SimpleDateFormat
import java.util.*

class Logger {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US)
        private val gson = Gson()

        fun log(s: String?) {
            if (s == null) { return }
            Log.d("Logger", s)

            if (BuildConfig.DEBUG) {
                val prefix = dateFormat.format(Date(System.currentTimeMillis()))
                val logs = arrayListOf<String>()
                logs.addAll(getLogs())
                logs.add("$prefix - $s")
                SmartStepsTrackerPrefs.setLogs(gson.toJson(LogEntries(logs)))
            }
        }

        fun getLogs(): List<String> {
            val logEntriesString = SmartStepsTrackerPrefs.getLogs()
            val logs =
                if (logEntriesString.isEmpty()) LogEntries(emptyList())
                else gson.fromJson(logEntriesString, LogEntries::class.java)
            return logs.entries.sorted()
        }

        fun clearAll() = SmartStepsTrackerPrefs.clearLogs()
    }
}
