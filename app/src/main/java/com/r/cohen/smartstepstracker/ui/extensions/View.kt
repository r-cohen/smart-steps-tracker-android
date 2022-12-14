package com.r.cohen.smartstepstracker.ui.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleGone")
fun View.bindVisibility(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}