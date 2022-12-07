package com.r.cohen.smartstepstracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val logsButtonClickEvent = MutableLiveData<ViewModelEvent<Boolean>>()
    val settingsClickEvent = MutableLiveData<ViewModelEvent<Boolean>>()

    fun onLogsButtonClick() {
        logsButtonClickEvent.postValue(ViewModelEvent(true))
    }

    fun onSettingsClick() {
        settingsClickEvent.postValue(ViewModelEvent(true))
    }
}