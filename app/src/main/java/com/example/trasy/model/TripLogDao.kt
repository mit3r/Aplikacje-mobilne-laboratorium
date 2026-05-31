package com.example.trasy.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripLogDao {
    @Insert
    suspend fun insert(tripLog: TripLog)

    @Delete
    suspend fun delete(tripLog: TripLog)

    @Query("SELECT * FROM trip_logs WHERE trailId = :trailId ORDER BY date DESC")
    fun getLogsForTrail(trailId: Int): Flow<List<TripLog>>
}
