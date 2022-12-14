package com.r.cohen.smartstepstracker.ui.month

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.DateTools
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import com.r.cohen.smartstepstracker.ui.extensions.configureDisplay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DateFormat
import java.util.*

class MonthViewModel: ViewModel() {
    val emptyState = MutableLiveData(true)
    private val stepsCountMonth = MutableLiveData(0)
    val formattedStepsCountMonth = MediatorLiveData<String>().apply {
        val observer = Observer<Int> { postValue(String.format("%,d", it)) }
        addSource(stepsCountMonth, observer)
    }
    val chartModel = MutableLiveData<AAChartModel>()
    private val subscriptions = ArrayList<Disposable>()

    fun subscribeToEvents() {
        StepsTrackerRepo.getMonthStepsCount { count -> stepsCountMonth.postValue(count) }
        subscriptions.addAll(listOf(
            StepsTrackerRepo.todayStepsCountChange
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    StepsTrackerRepo.getMonthStepsCount { count -> stepsCountMonth.postValue(count) }
                }, { e -> Logger.log(e.message) })
        ))

        StepsTrackerRepo.getMonthMeasures { measures ->
            val hasOnlyZeros = measures.none { it.stepsCount > 0 }

            if (measures.isEmpty() || hasOnlyZeros) {
                emptyState.postValue(true)
                return@getMonthMeasures
            }

            val points = ArrayList<Array<*>>()
            val cal = Calendar.getInstance()
            cal.timeInMillis = DateTools.get30DaysAgo(System.currentTimeMillis())
            while (!DateTools.isSameDay(cal.timeInMillis, System.currentTimeMillis())) {
                val count = measures
                    .filter { DateTools.isSameDay(it.timestamp, cal.timeInMillis) }
                    .sumOf { it.stepsCount }
                points.add(arrayOf(formattedDate(cal.timeInMillis), count))
                cal.add(Calendar.DATE, 1)
            }

            if (points.size <= 1) {
                emptyState.postValue(true)
                return@getMonthMeasures
            }

            val model = AAChartModel().apply {
                configureDisplay()
                series(arrayOf(
                    AASeriesElement().apply {
                        configureDisplay()
                        data(points.toTypedArray())
                    }
                ))
            }

            emptyState.postValue(false)
            chartModel.postValue(model)
        }
    }

    fun unsubscribeEvents() {
        subscriptions.filter { !it.isDisposed }.forEach { it.dispose() }
    }

    private fun formattedDate(timestamp: Long): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}