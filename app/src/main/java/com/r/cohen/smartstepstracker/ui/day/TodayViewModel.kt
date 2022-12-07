package com.r.cohen.smartstepstracker.ui.day

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class TodayViewModel: ViewModel() {
    private val stepsCountToday = MutableLiveData(0)
    val formattedStepsCountToday = MediatorLiveData<String>().apply {
        val observer = Observer<Int> { postValue(String.format("%,d", it)) }
        addSource(stepsCountToday, observer)
    }
    val measurementsDataSet = MutableLiveData<LineDataSet>()
    private val subscriptions = ArrayList<Disposable>()

    fun subscribeToEvents() {
        StepsTrackerRepo.getTodayStepsCount { count ->
            stepsCountToday.postValue(count)
        }
        subscriptions.addAll(listOf(
            StepsTrackerRepo.todayStepsCountChange
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ count ->
                    stepsCountToday.postValue(count)
                }, { e -> Logger.log(e.message) })
        ))

        StepsTrackerRepo.getTodayMeasures { measures ->
            val dataset = measures.map { measure ->
                Entry(measure.timestamp.toFloat(), measure.stepsCount.toFloat())
            }

            measurementsDataSet.postValue(
                LineDataSet(
                    measures.map { measure ->
                        Entry(measure.timestamp.toFloat(), measure.stepsCount.toFloat())
                    }, ""
                ),
            )
        }
    }

    fun unsubscribeEvents() {
        subscriptions.filter { !it.isDisposed }.forEach { it.dispose() }
    }
}