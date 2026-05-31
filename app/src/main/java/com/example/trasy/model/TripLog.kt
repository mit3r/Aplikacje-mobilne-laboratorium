package com.example.trasy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_logs")
data class TripLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trailId: Int,
    val timeInSeconds: Long,
    val date: Long = System.currentTimeMillis()
)
