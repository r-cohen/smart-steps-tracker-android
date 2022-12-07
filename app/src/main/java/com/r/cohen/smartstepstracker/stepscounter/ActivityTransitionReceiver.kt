package com.r.cohen.smartstepstracker.stepscounter

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.r.cohen.smartstepstracker.BuildConfig
import com.r.cohen.smartstepstracker.logger.Logger

class ActivityTransitionReceiver: BroadcastReceiver() {
    companion object {
        private const val RECEIVER_ACTION = "${BuildConfig.APPLICATION_ID}.TRANSITIONS_RECEIVER_ACTION"
        private const val REQUEST_CODE = 11417

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(RECEIVER_ACTION)
            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flag)
        }

        fun getIntentFilter(): IntentFilter = IntentFilter(RECEIVER_ACTION)

        fun enableActivityTransitions(context: Context, onDone: (Boolean) -> Unit) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val request = ActivityTransitionWalkRequest.build()
            ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, getPendingIntent(context))
                .apply {
                    addOnSuccessListener { onDone.invoke(true) }
                    addOnFailureListener { onDone.invoke(false) }
                }

        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != RECEIVER_ACTION) {
            return
        }
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult.extractResult(intent)?.transitionEvents?.forEach { event ->
                Logger.log("activity ${event.activityType} transition ${event.transitionType}")

                val serviceIntent = Intent(context, StepCounterListenerService::class.java)
                if ((event.activityType == DetectedActivity.WALKING ||
                            event.activityType == DetectedActivity.RUNNING) &&
                    event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    context?.startForegroundService(serviceIntent)
                } else {
                    context?.stopService(serviceIntent)
                }
            }
        }
    }
}