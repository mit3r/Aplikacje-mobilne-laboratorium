package com.example.pierwszaaplikacja.model


enum class TrailType {
    RUNNING,
    CYCLING,
}

data class Trail(
    val id: Int,
    val title: String,
    val distance: Float,
    val type: TrailType,
    val description: String
)
