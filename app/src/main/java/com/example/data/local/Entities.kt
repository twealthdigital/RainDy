package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_tasks")
data class WeatherTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Outdoor",
    val preferredClimate: String = "SUNNY", // "SUNNY", "RAIN_SAFE", "ANY", "INDOOR"
    val scheduledTime: String = "10:00 AM",
    val scheduledDay: String = "Today",
    val isCompleted: Boolean = false,
    val priority: String = "Normal",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey
    val locationKey: String = "default_location",
    val cityName: String,
    val temperature: Double,
    val conditionText: String,
    val weatherCode: Int,
    val rainProbability: Int,
    val uvIndex: Double,
    val windSpeed: Double,
    val nextRainNotice: String,
    val nextSunNotice: String,
    val rawJson: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
