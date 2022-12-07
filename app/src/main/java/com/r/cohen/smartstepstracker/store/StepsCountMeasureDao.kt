package com.r.cohen.smartstepstracker.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StepsCountMeasureDao {
    @Insert
    suspend fun saveMeasure(measure: StepsCountMeasure)

    @Query("SELECT SUM(steps_count) FROM stepscountmeasure WHERE timestamp >= :timestamp")
    suspend fun getCountFromTimestamp(timestamp: Long): Int?

    @Query("SELECT * FROM stepscountmeasure ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMeasure(): StepsCountMeasure?

    @Query("SELECT * FROM stepscountmeasure WHERE timestamp >= :timestamp ORDER BY timestamp")
    suspend fun getMeasuresFromTimestamp(timestamp: Long): List<StepsCountMeasure>

    @Query("DELETE FROM stepscountmeasure")
    suspend fun deleteAllMeasures()
}