package com.r.cohen.smartstepstracker.stepscounter

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import com.r.cohen.smartstepstracker.SmartStepsTrackerApp
import com.r.cohen.smartstepstracker.logger.Logger
import java.util.concurrent.TimeUnit

class StepCountSchedulerService : JobService() {
    companion object {
        private const val JOB_ID = 101111
        val stepsQueryInterval = TimeUnit.HOURS.toMillis(1)

        fun schedule(context: Context) {
            Logger.log("StepCountSchedulerService.schedule")

            cancelScheduledJobs(context)

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(JOB_ID, ComponentName(context, StepCountSchedulerService::class.java))
            val job = jobInfo
                .setPersisted(true)
                .setPeriodic(stepsQueryInterval)
                .build()
            jobScheduler.schedule(job)
        }

        private fun cancelScheduledJobs(context: Context) {
            Logger.log("StepCountSchedulerService.cancelScheduledJobs")
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Logger.log("onStartJob")
        SmartStepsTrackerApp.instance.registerActivityReceiver()
        return false //true -> keep running service
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Logger.log("onStopJob")
        // do we need to unregister broadcast receiver here?
        return true
    }

}
