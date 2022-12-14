package com.r.cohen.smartstepstracker.ui.extensions

import androidx.databinding.BindingAdapter
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

@BindingAdapter("bindChartModel")
fun AAChartView.bindWithModel(model: AAChartModel?) {
    if (model == null) { return }
    aa_drawChartWithChartModel(model)
}