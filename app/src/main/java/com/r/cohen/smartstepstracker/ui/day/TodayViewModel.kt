package com.r.cohen.smartstepstracker.ui.day

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import com.r.cohen.smartstepstracker.store.StepsCountMeasure
import com.r.cohen.smartstepstracker.ui.extensions.configureDisplay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class TodayViewModel: ViewModel() {
    val emptyState = MutableLiveData(true)
    private val stepsCountToday = MutableLiveData(0)
    val formattedStepsCountToday = MediatorLiveData<String>().apply {
        val observer = Observer<Int> { postValue(String.format("%,d", it)) }
        addSource(stepsCountToday, observer)
    }
    val chartModel = MutableLiveData<AAChartModel>()
    private val subscriptions = ArrayList<Disposable>()

    fun subscribeToEvents() {
        StepsTrackerRepo.getTodayStepsCount { count -> stepsCountToday.postValue(count) }
        subscriptions.addAll(listOf(
            StepsTrackerRepo.todayStepsCountChange
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ count ->
                    stepsCountToday.postValue(count)
                }, { e -> Logger.log(e.message) })
        ))

        StepsTrackerRepo.getTodayMeasures { measures ->
            val hasOnlyZeros = measures.none { it.stepsCount > 0 }

            if (measures.size >= 2 && !hasOnlyZeros) {
                emptyState.postValue(false)

                val points = ArrayList<Array<*>>()
                points.addAll(measures.map { getPointFromMeasure(it) }.toTypedArray())

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

    private fun getPointFromMeasure(measure: StepsCountMeasure): Array<Any> {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val time = timeFormat.format(Date(measure.timestamp))
        return arrayOf(time, measure.stepsCount)
    }
}