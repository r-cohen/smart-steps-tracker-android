package com.r.cohen.smartstepstracker.ui.extensions

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel

fun AAChartModel.configureDisplay() {
    backgroundColor("#00000000")
    dataLabelsEnabled(false)
    legendEnabled(false)
    yAxisVisible(false)
    xAxisVisible(false)
}