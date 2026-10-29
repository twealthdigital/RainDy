package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    @Json(name = "current_weather") val currentWeather: CurrentWeatherData? = null,
    val hourly: HourlyWeatherData? = null,
    val daily: DailyWeatherData? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherData(
    val temperature: Double = 0.0,
    val windspeed: Double = 0.0,
    val winddirection: Double = 0.0,
    val weathercode: Int = 0,
    val is_day: Int = 1,
    val time: String = ""
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherData(
    val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature2m: List<Double> = emptyList(),
    @Json(name = "relativehumidity_2m") val relativeHumidity2m: List<Int> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int> = emptyList(),
    @Json(name = "weathercode") val weatherCode: List<Int> = emptyList(),
    @Json(name = "uv_index") val uvIndex: List<Double> = emptyList(),
    @Json(name = "windspeed_10m") val windspeed10m: List<Double> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyWeatherData(
    val time: List<String> = emptyList(),
    @Json(name = "weathercode") val weatherCode: List<Int> = emptyList(),
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double> = emptyList(),
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList(),
    @Json(name = "uv_index_max") val uvIndexMax: List<Double> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeoLocation>? = null
)

@JsonClass(generateAdapter = true)
data class GeoLocation(
    val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String? = null,
    @Json(name = "admin1") val adminRegion: String? = null
)

// UI and Domain Models
enum class ClimateType(val label: String, val icon: String) {
    RAIN("Rainy", "🌧️"),
    DRIZZLE("Light Rain", "🌦️"),
    THUNDERSTORM("Thunderstorm", "⛈️"),
    SUNNY("Clear & Sunny", "☀️"),
    CLOUDY("Overcast", "☁️"),
    SNOW("Snow", "❄️"),
    WINDY("High Winds", "💨"),
    EXTREME_HEAT("Extreme Heat", "🔥")
}

data class NextWeatherEventPrediction(
    val type: ClimateType,
    val title: String,
    val timeDescription: String,
    val hoursFromNow: Int,
    val probability: Int,
    val advisory: String,
    val formattedTime: String
)

data class GlobalClimateTrendPoint(
    val year: Int,
    val temperatureAnomaly: Double, // in °C relative to 1850-1900
    val co2Ppm: Double,
    val arcticIceAnomalyMillionKm2: Double
)

data class OutdoorActivityRecommendation(
    val activityName: String,
    val icon: String,
    val bestTimeWindow: String,
    val weatherSuitability: String, // "Optimal", "Good", "Avoid"
    val reason: String,
    val startHourOffset: Int,
    val durationHours: Int = 2
)
