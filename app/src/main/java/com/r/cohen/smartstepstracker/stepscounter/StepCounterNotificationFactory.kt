package com.r.cohen.smartstepstracker.stepscounter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.r.cohen.smartstepstracker.ui.MainActivity
import com.r.cohen.smartstepstracker.R

private const val channelId = "SmartStepsTrackerNotificationChannel"
private const val channelName = "SmartStepsTrackerNotificationChannel"

class StepCounterNotificationFactory {
    companion object {
        const val ongoingNotificationId = 713705

        fun build(context: Context): Notification {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)

            val mainActivityIntent = Intent(context, MainActivity::class.java)
            val startMainPendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, mainActivityIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            val stopTrackingIntent = Intent(context, StepCounterListenerService::class.java)
            stopTrackingIntent.action = StepCounterListenerService.stopTrackingAction
            val stopTrackingPendingIntent: PendingIntent =
                PendingIntent.getService(context, 0, stopTrackingIntent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.barefoot)
                .setContentIntent(startMainPendingIntent)
                .setContentTitle(context.getString(R.string.notification_content))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.ic_baseline_stop_24, context.getString(R.string.stop_tracking), stopTrackingPendingIntent)
                .setOngoing(true)
            return builder.build()
        }
    }
}