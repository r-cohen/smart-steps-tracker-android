package com.r.cohen.smartstepstracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val settingsClickEvent = MutableLiveData<ViewModelEvent<Boolean>>()

    fun onSettingsClick() {
        settingsClickEvent.postValue(ViewModelEvent(true))
    }
}