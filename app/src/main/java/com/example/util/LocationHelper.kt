package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

data class DetectedLocation(
    val cityName: String,
    val regionState: String,
    val country: String,
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
    val isGps: Boolean
)

object LocationHelper {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    /**
     * Intelligently detects the user's real location:
     * 1. First attempts device GPS / FusedLocation.
     * 2. If coordinates are found and NOT default emulator Googleplex coords (37.7749, -122.4194),
     *    reverse-geocodes with Android Geocoder to get the real Town, State, and Country.
     * 3. If GPS is unavailable, null, or is the emulator default, calls IP-based geolocation
     *    which instantly returns the user's real physical city, region/state, and country!
     */
    suspend fun detectBestLocation(context: Context): DetectedLocation? = withContext(Dispatchers.IO) {
        // Attempt GPS first
        val gpsCoords = getGpsCoordinates(context)
        if (gpsCoords != null) {
            val (lat, lon) = gpsCoords
            // If coords are suspiciously equal to the San Francisco emulator default, check IP location
            val isEmulatorDefault = Math.abs(lat - 37.7749) < 0.05 && Math.abs(lon - (-122.4194)) < 0.05
            if (!isEmulatorDefault) {
                val resolved = reverseGeocode(context, lat, lon)
                if (resolved != null) {
                    return@withContext resolved.copy(isGps = true)
                }
            }
        }

        // Fallback to IP Geolocation for real physical location
        val ipLoc = fetchIpGeolocation()
        if (ipLoc != null) {
            return@withContext ipLoc
        }

        // If IP failed but we have GPS coords (even if emulator), use them
        if (gpsCoords != null) {
            val resolved = reverseGeocode(context, gpsCoords.first, gpsCoords.second)
            if (resolved != null) return@withContext resolved.copy(isGps = true)
        }

        null
    }

    @SuppressLint("MissingPermission")
    suspend fun getGpsCoordinates(context: Context): Pair<Double, Double>? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()

                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(Pair(location.latitude, location.longitude))
                        } else {
                            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                            val lastGps = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                ?: lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (lastGps != null) {
                                continuation.resume(Pair(lastGps.latitude, lastGps.longitude))
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }

                continuation.invokeOnCancellation {
                    cts.cancel()
                }
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }

    fun reverseGeocode(context: Context, lat: Double, lon: Double): DetectedLocation? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val address = addresses?.firstOrNull()
            if (address != null) {
                val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Local Area"
                val state = address.adminArea ?: ""
                val country = address.countryName ?: ""
                val display = buildString {
                    append(city)
                    if (country.isNotBlank()) append(", $country")
                }
                DetectedLocation(
                    cityName = city,
                    regionState = state,
                    country = country,
                    displayName = display,
                    latitude = lat,
                    longitude = lon,
                    isGps = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * IP Geolocation fallback when GPS is unavailable or emulator returns San Francisco.
     * Uses free IP geolocation to resolve user's real physical town/city and country.
     */
    private fun fetchIpGeolocation(): DetectedLocation? {
        // Try freeipapi.com
        try {
            val request = Request.Builder()
                .url("https://freeipapi.com/api/json")
                .header("User-Agent", "RainDy-WeatherApp")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use null
                    val json = JSONObject(body)
                    val city = json.optString("cityName", "").takeIf { it.isNotBlank() } ?: "City"
                    val state = json.optString("regionName", "")
                    val country = json.optString("countryName", "")
                    val lat = json.optDouble("latitude", 0.0)
                    val lon = json.optDouble("longitude", 0.0)

                    if (lat != 0.0 && lon != 0.0) {
                        val display = buildString {
                            append(city)
                            if (country.isNotBlank()) append(", $country")
                        }
                        return DetectedLocation(
                            cityName = city,
                            regionState = state,
                            country = country,
                            displayName = display,
                            latitude = lat,
                            longitude = lon,
                            isGps = false
                        )
                    }
                }
            }
        } catch (ignored: Exception) {}

        // Fallback to ipapi.co
        try {
            val request = Request.Builder()
                .url("https://ipapi.co/json/")
                .header("User-Agent", "RainDy-WeatherApp")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use null
                    val json = JSONObject(body)
                    val city = json.optString("city", "").takeIf { it.isNotBlank() } ?: "City"
                    val state = json.optString("region", "")
                    val country = json.optString("country_name", "")
                    val lat = json.optDouble("latitude", 0.0)
                    val lon = json.optDouble("longitude", 0.0)

                    if (lat != 0.0 && lon != 0.0) {
                        val display = buildString {
                            append(city)
                            if (country.isNotBlank()) append(", $country")
                        }
                        return DetectedLocation(
                            cityName = city,
                            regionState = state,
                            country = country,
                            displayName = display,
                            latitude = lat,
                            longitude = lon,
                            isGps = false
                        )
                    }
                }
            }
        } catch (ignored: Exception) {}

        return null
    }
}
