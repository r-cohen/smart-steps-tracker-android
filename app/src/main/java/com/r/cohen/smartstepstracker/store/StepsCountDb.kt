package com.r.cohen.smartstepstracker.store

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepsCountMeasure::class], version = 1, exportSchema = false)
abstract class StepsCountDb: RoomDatabase() {
    abstract fun stepsCountMeasureDao(): StepsCountMeasureDao
}