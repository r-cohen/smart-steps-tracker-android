package com.r.cohen.smartstepstracker.repo

import com.r.cohen.smartstepstracker.SmartStepsTrackerApp
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.stepscounter.StepCountSchedulerService
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs
import com.r.cohen.smartstepstracker.store.StepsCountMeasure
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object StepsTrackerRepo {
    private val dbDao = SmartStepsTrackerApp.stepsCountStore.stepsCountMeasureDao()
    val todayStepsCountChange: PublishSubject<Int> = PublishSubject.create()
    private val mutex = Mutex()

    fun saveStepsCount(totalCount: Int) = CoroutineScope(Dispatchers.IO).launch {
        try {
            saveSteps(totalCount)
        } catch (e: Exception) {
            Logger.log(e.message)
        }
    }

    private suspend fun saveSteps(totalCount: Int) = mutex.withLock {
        val now = System.currentTimeMillis()
        var delta = SmartStepsTrackerPrefs.getStepsCountLastValue()?.let { previous ->
            if (DateTools.isSameDay(now, previous.timestamp)) totalCount - previous.stepsCount
            else 0
        } ?: 0
        if (delta < 0) {
            delta = 0
        }
        SmartStepsTrackerPrefs.setStepsCountLastValue(totalCount)

        val previousCumul = SmartStepsTrackerPrefs.getStepsCountCumulativeDelta()
        val previousDelta = previousCumul?.let { previous ->
            if (DateTools.isSameDay(now, previous.timestamp)) previous.stepsCount
            else 0
        } ?: 0

        val cumulativeDelta = previousDelta + delta

        val lastMeasure = dbDao.getLastMeasure()
        if (lastMeasure == null) {
            dbDao.saveMeasure(StepsCountMeasure(now, cumulativeDelta))
            SmartStepsTrackerPrefs.setStepsCountCumulativeDelta(0)
        } else {
            val frequency = StepCountSchedulerService.stepsQueryInterval
            if (now - lastMeasure.timestamp >= frequency) {
                dbDao.saveMeasure(StepsCountMeasure(now, cumulativeDelta))
                SmartStepsTrackerPrefs.setStepsCountCumulativeDelta(0)
            } else {
                SmartStepsTrackerPrefs.setStepsCountCumulativeDelta(cumulativeDelta)
            }
        }
        getTodayStepsCount { count -> todayStepsCountChange.onNext(count) }
    }

    fun getTodayStepsCount(onComplete: (Int) -> Unit)  {
        getCountFromTimeStamp(DateTools.getStartOfDayTimeStamp(), onComplete)
    }

    fun getWeekStepsCount(onComplete: (Int) -> Unit) {
        val startOfDay = DateTools.getStartOfDayTimeStamp()
        val firstDayOfWeek = DateTools.getFirstDayOfWeekFrom(startOfDay)
        getCountFromTimeStamp(firstDayOfWeek, onComplete)
    }

    fun getMonthStepsCount(onComplete: (Int) -> Unit) {
        val startOfDay = DateTools.getStartOfDayTimeStamp()
        val lastMonth = DateTools.get30DaysAgo(startOfDay)
        getCountFromTimeStamp(lastMonth, onComplete)
    }

    fun getTodayMeasures(onComplete: (List<StepsCountMeasure>) -> Unit) {
        val startOfDay = DateTools.getStartOfDayTimeStamp()
        getMeasuresFromtTimeStamp(startOfDay, onComplete)
    }

    fun getWeekMeasures(onComplete: (List<StepsCountMeasure>) -> Unit) {
        val startOfDay = DateTools.getStartOfDayTimeStamp()
        val firstDayOfWeek = DateTools.getFirstDayOfWeekFrom(startOfDay)
        getMeasuresFromtTimeStamp(firstDayOfWeek, onComplete)
    }

    fun getMonthMeasures(onComplete: (List<StepsCountMeasure>) -> Unit) {
        val startOfDay = DateTools.getStartOfDayTimeStamp()
        val lastMonth = DateTools.get30DaysAgo(startOfDay)
        getMeasuresFromtTimeStamp(lastMonth, onComplete)
    }

    fun deleteAll(onComplete: (Boolean) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        try {
            SmartStepsTrackerPrefs.clearAll()
            dbDao.deleteAllMeasures()
            onComplete(true)
            return@launch
        } catch (e: Exception) {
            Logger.log(e.message)
        }
        onComplete(false)
    }

    private fun getCountFromTimeStamp(timestamp: Long, onComplete: (Int) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val sumMeasures = dbDao.getCountFromTimestamp(timestamp) ?: 0

            val previousCumul = SmartStepsTrackerPrefs.getStepsCountCumulativeDelta()
            val previousDelta = previousCumul?.let { previous ->
                if (DateTools.isSameDay(System.currentTimeMillis(), previous.timestamp)) previous.stepsCount
                else 0
            } ?: 0

            val count = sumMeasures + previousDelta
            onComplete.invoke(count)
            return@launch
        } catch (e: Exception) {
            Logger.log(e.message)
        }
        onComplete.invoke(0)
    }

    private fun getMeasuresFromtTimeStamp(timestamp: Long, onComplete: (List<StepsCountMeasure>) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val measures = dbDao.getMeasuresFromTimestamp(timestamp)
            onComplete.invoke(measures)
            return@launch
        } catch (e: Exception) {
            Logger.log(e.message)
        }
        onComplete.invoke(emptyList())
    }

}