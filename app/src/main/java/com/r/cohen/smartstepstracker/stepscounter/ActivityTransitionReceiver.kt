package com.r.cohen.smartstepstracker.stepscounter

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
            val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                //PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            intent.setPackage(context.packageName)
            return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flag)
        }

        fun getIntentFilter(): IntentFilter = IntentFilter(RECEIVER_ACTION)

        fun enableActivityTransitions(context: Context, onDone: (Boolean) -> Unit) {
            Logger.log("enableActivityTransitions")
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Logger.log("enableActivityTransitions permissions not granted")
                return
            }

            val request = ActivityTransitionWalkRequest.build()
            Logger.log("enableActivityTransitions built request, getClient")
            val client = ActivityRecognition.getClient(context)
            Logger.log("enableActivityTransitions removeActivityTransitionUpdates")
            client.removeActivityTransitionUpdates(getPendingIntent(context))
                .addOnCompleteListener {
                    Logger.log("enableActivityTransitions removeActivityTransitionUpdates complete => requestActivityTransitionUpdates")
                    client
                        //.requestActivityUpdates(StepCountSchedulerService.stepsQueryInterval - 1, getPendingIntent(context))
                        .requestActivityTransitionUpdates(request, getPendingIntent(context))
                        .apply {
                            addOnSuccessListener {
                                Logger.log("requestActivityTransitionUpdates success")
                                onDone.invoke(true)
                            }
                            addOnFailureListener {
                                Logger.log("requestActivityTransitionUpdates failed ${it.message}")
                                onDone.invoke(false)
                            }
                        }
                }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.log("onReceive called ${intent?.action}")
        if (intent?.action != RECEIVER_ACTION) {
            return
        }
        Logger.log("onReceive action is valid. intent: ${intent.toString()}")
        if (ActivityTransitionResult.hasResult(intent)) {
            Logger.log("onReceive intent hasResult")
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