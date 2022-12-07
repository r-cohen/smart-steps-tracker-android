package com.r.cohen.smartstepstracker.ui.month

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

class MonthViewModel: ViewModel() {
    private val stepsCountMonth = MutableLiveData(0)
    val formattedStepsCountMonth = MediatorLiveData<String>().apply {
        val observer = Observer<Int> { postValue(String.format("%,d", it)) }
        addSource(stepsCountMonth, observer)
    }
    val measurementsDataSet = MutableLiveData<LineDataSet>()
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