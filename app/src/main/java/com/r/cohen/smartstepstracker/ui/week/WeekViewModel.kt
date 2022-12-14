package com.r.cohen.smartstepstracker.ui.week

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.r.cohen.smartstepstracker.R
import com.r.cohen.smartstepstracker.SmartStepsTrackerApp
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import com.r.cohen.smartstepstracker.ui.extensions.configureDisplay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class WeekViewModel: ViewModel() {
    val emptyState = MutableLiveData(true)
    private val stepsCountWeek = MutableLiveData(0)
    val formattedStepsCountWeek = MediatorLiveData<String>().apply {
        val observer = Observer<Int> { postValue(String.format("%,d", it)) }
        addSource(stepsCountWeek, observer)
    }
    val chartModel = MutableLiveData<AAChartModel>()
    private val subscriptions = ArrayList<Disposable>()

    fun subscribeToEvents() {
        StepsTrackerRepo.getWeekStepsCount { count -> stepsCountWeek.postValue(count) }
        subscriptions.addAll(listOf(
            StepsTrackerRepo.todayStepsCountChange
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    StepsTrackerRepo.getWeekStepsCount { count -> stepsCountWeek.postValue(count) }
                }, { e -> Logger.log(e.message) })
        ))

        StepsTrackerRepo.getWeekMeasures { measures ->
            val hasOnlyZeros = measures.none { it.stepsCount > 0 }

            if (measures.isNotEmpty() && !hasOnlyZeros) {
                emptyState.postValue(false)

                val points = ArrayList<Array<*>>()
                for (dow in Calendar.SUNDAY..Calendar.SATURDAY) {
                    val count = measures
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.timestamp
                            cal.get(Calendar.DAY_OF_WEEK) == dow
                        }
                        .sumOf { it.stepsCount }
                    points.add(arrayOf(getDayOfWeekText(dow), count))
                }

                if (points.size <= 1) {
                    emptyState.postValue(true)
                    return@getWeekMeasures
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

                chartModel.postValue(model)
            } else {
                emptyState.postValue(true)
            }
        }
    }

    fun unsubscribeEvents() {
        subscriptions.filter { !it.isDisposed }.forEach { it.dispose() }
    }

    private fun getDayOfWeekText(dayOfWeek: Int): String {
        val resources = SmartStepsTrackerApp.instance.resources
        return when (dayOfWeek) {
            Calendar.MONDAY -> resources.getString(R.string.monday)
            Calendar.TUESDAY -> resources.getString(R.string.tuesday)
            Calendar.WEDNESDAY -> resources.getString(R.string.wednesday)
            Calendar.THURSDAY -> resources.getString(R.string.thursday)
            Calendar.FRIDAY -> resources.getString(R.string.friday)
            Calendar.SATURDAY -> resources.getString(R.string.saturday)
            else -> resources.getString(R.string.sunday)
        }
    }
}