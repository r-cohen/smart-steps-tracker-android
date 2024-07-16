package com.r.cohen.smartstepstracker.stepscounter

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo

class StepCounterListenerService: Service() {
    companion object {
        const val stopTrackingAction = "stopTrackingAction"
    }

    private val binder = StepCounterListenerServiceBinder(this)
    private var stepListener: SensorEventListener? = null

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Logger.log("StepCounterListenerService.onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.log("StepCounterListenerService onStartCommand")
        if (intent?.action == stopTrackingAction) {
            Logger.log("StepCounterListenerService stop action")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            val notification = StepCounterNotificationFactory.build(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(StepCounterNotificationFactory.ongoingNotificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
            } else {
                startForeground(StepCounterNotificationFactory.ongoingNotificationId, notification)
            }

            if (stepListener == null) {
                stepListener = object: SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?.values?.firstOrNull()?.toInt()?.let { count ->
                            StepsTrackerRepo.saveStepsCount(count)
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }

                Logger.log("StepCounterListenerService sensorManager.registerListener")
                val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                val requested = sensorManager.registerListener(
                    stepListener,
                    stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                Logger.log("StepCounterListenerService requested: $requested")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        Logger.log("StepCounterListenerService.onDestroy")
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepListener?.let { sensorManager.unregisterListener(it) }
        stepListener = null
    }
}

class StepCounterListenerServiceBinder(val service: StepCounterListenerService): Binder()