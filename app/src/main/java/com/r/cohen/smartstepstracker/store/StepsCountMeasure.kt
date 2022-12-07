package com.r.cohen.smartstepstracker.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepsCountMeasure(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "steps_count") val stepsCount: Int
)
