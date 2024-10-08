package com.r.cohen.smartstepstracker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.stepscounter.ActivityTransitionReceiver
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs
import com.r.cohen.smartstepstracker.store.StepsCountDb

class SmartStepsTrackerApp: Application() {
    companion object {
        lateinit var prefs: SharedPreferences
        lateinit var instance: SmartStepsTrackerApp
        lateinit var stepsCountStore: StepsCountDb
    }

    private var transitionsReceiver: ActivityTransitionReceiver? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        stepsCountStore = Room.databaseBuilder(this, StepsCountDb::class.java, "SmartStepsTrackerDb")
            .build()
        prefs = getSharedPreferences("stepstrackprefs", Context.MODE_PRIVATE)
        setAppTheme(SmartStepsTrackerPrefs.getSelectedThemeValue())
    }

    fun registerActivityReceiver() {
        Logger.log("registerActivityReceiver")

        ActivityTransitionReceiver.enableActivityTransitions(this) { success ->
            Logger.log("registerActivityReceiver callback result: $success")
            if (!success) {
                return@enableActivityTransitions
            }

            unregisterActivityReceiver()

            if (transitionsReceiver == null) {
                transitionsReceiver = ActivityTransitionReceiver()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(transitionsReceiver, ActivityTransitionReceiver.getIntentFilter(),
                    RECEIVER_EXPORTED)
            } else {
                registerReceiver(transitionsReceiver, ActivityTransitionReceiver.getIntentFilter())
            }
            Logger.log("registerActivityReceiver done")
        }
    }

    fun unregisterActivityReceiver() {
        Logger.log("unregisterActivityReceiver")
        if (transitionsReceiver != null) {
            try {
                unregisterReceiver(transitionsReceiver)
                transitionsReceiver = null
            } catch (e: Exception) {
                e.message?.let { Log.d("dbg", it) }
            }
        }
    }

    fun setAppTheme(themeValue: String) {
        Logger.log("setting theme value to $themeValue")
        AppCompatDelegate.setDefaultNightMode(
            when (themeValue) {
                "1" -> AppCompatDelegate.MODE_NIGHT_YES
                "2" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}