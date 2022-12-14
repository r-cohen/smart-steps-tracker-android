package com.r.cohen.smartstepstracker.ui.extensions

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

fun AASeriesElement.configureDisplay() {
    name("steps")
    color("#c0c0c0")
    lineWidth(4)
    type(AAChartType.Areaspline)
}