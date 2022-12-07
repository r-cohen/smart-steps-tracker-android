package com.r.cohen.smartstepstracker.ui

open class ViewModelEvent<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun handle(): T? = if (hasBeenHandled) null else {
        hasBeenHandled = true
        content
    }

    fun peek(): T = content
}