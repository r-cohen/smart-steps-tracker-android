package com.r.cohen.smartstepstracker.logger

import com.google.gson.annotations.SerializedName

data class LogEntries(
    @SerializedName("entries")
    val entries: List<String>
)
