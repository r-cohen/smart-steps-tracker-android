package com.r.cohen.smartstepstracker.stepscounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs

class StartUpBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            SmartStepsTrackerPrefs.clearStepsCountLastValue()
            context?.let { StepCountSchedulerService.schedule(it) }
        }
    }
}