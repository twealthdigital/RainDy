package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.WeatherApiService
import com.example.data.local.AppDatabase
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.WeatherTaskEntity
import com.example.data.model.ClimateType
import com.example.data.model.GeoLocation
import com.example.data.model.GlobalClimateTrendPoint
import com.example.data.model.NextWeatherEventPrediction
import com.example.data.model.OutdoorActivityRecommendation
import com.example.data.model.WeatherResponse
import com.example.data.repository.WeatherRepository
import com.example.ui.components.MorphismStyle
import com.example.ui.components.WeatherArtType
import com.example.ui.theme.ExtremeHeatOrange
import com.example.ui.theme.FreezeGlacialBlue
import com.example.ui.theme.RainDyApricot
import com.example.ui.theme.TemperateTeal
import com.example.util.CalendarHelper
import com.example.util.LocationHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class UserProfile(
    val displayName: String = "Guest User",
    val email: String = "",
    val isGoogleSignedIn: Boolean = false,
    val avatarInitials: String = "GU"
)

data class NotificationPreferences(
    val rainAlertThresholdPercent: Int = 40,
    val uvAlertThreshold: Double = 6.0,
    val highWindSpeedKmh: Double = 30.0,
    val enableSoundAndVibrate: Boolean = true,
    val autoCalendarSync: Boolean = true
)

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val data: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class RainDyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository

    private val _currentCity = MutableStateFlow("Locating...")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _latitude = MutableStateFlow(0.0)
    private val _longitude = MutableStateFlow(0.0)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _liveTemperature = MutableStateFlow<Double?>(null)
    val liveTemperature: StateFlow<Double?> = _liveTemperature.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    val cachedWeather: StateFlow<CachedWeatherEntity?>
    val tasks: StateFlow<List<WeatherTaskEntity>>

    private val _nextRainPrediction = MutableStateFlow<NextWeatherEventPrediction?>(null)
    val nextRainPrediction: StateFlow<NextWeatherEventPrediction?> = _nextRainPrediction.asStateFlow()

    private val _nextSunPrediction = MutableStateFlow<NextWeatherEventPrediction?>(null)
    val nextSunPrediction: StateFlow<NextWeatherEventPrediction?> = _nextSunPrediction.asStateFlow()

    private val _activityRecommendations = MutableStateFlow<List<OutdoorActivityRecommendation>>(emptyList())
    val activityRecommendations: StateFlow<List<OutdoorActivityRecommendation>> = _activityRecommendations.asStateFlow()

    val globalClimatePoints: List<GlobalClimateTrendPoint>

    private val _activeBubbleAlert = MutableStateFlow<NextWeatherEventPrediction?>(null)
    val activeBubbleAlert: StateFlow<NextWeatherEventPrediction?> = _activeBubbleAlert.asStateFlow()

    private val _isBubbleVisible = MutableStateFlow(false)
    val isBubbleVisible: StateFlow<Boolean> = _isBubbleVisible.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Morphism Style: Defaults to Solid Modern Morphism, with toggle to Liquid Glass
    private val _morphismStyle = MutableStateFlow(MorphismStyle.SOLID_MODERN)
    val morphismStyle: StateFlow<MorphismStyle> = _morphismStyle.asStateFlow()

    // Dynamic Climate-Adaptive Theme Color (Freezing Blue <= 5°C, Spring Teal 6-19°C, RainDy Apricot #f5a158 20-28°C, Hot Orange > 28°C)
    private val _climateThemeColor = MutableStateFlow<Color>(RainDyApricot)
    val climateThemeColor: StateFlow<Color> = _climateThemeColor.asStateFlow()

    private val _weatherArtType = MutableStateFlow<WeatherArtType>(WeatherArtType.SUN)
    val weatherArtType: StateFlow<WeatherArtType> = _weatherArtType.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val isUserSignedIn: Boolean
        get() = _userProfile.value.isGoogleSignedIn

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeoLocation>>(emptyList())
    val searchResults: StateFlow<List<GeoLocation>> = _searchResults.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow("Just now")
    val lastUpdatedTime: StateFlow<String> = _lastUpdatedTime.asStateFlow()

    private val _isGpsActive = MutableStateFlow(false)
    val isGpsActive: StateFlow<Boolean> = _isGpsActive.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val api = WeatherApiService.create()
        repository = WeatherRepository(api, database)

        cachedWeather = repository.cachedWeather
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        tasks = repository.allTasks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        globalClimatePoints = repository.getGlobalClimateTrends()

        // Seed initial offline tasks if empty
        viewModelScope.launch {
            seedInitialTasksIfEmpty()
        }

        // 1. Immediately Auto-detect the user's real location (GPS or IP geolocation)
        viewModelScope.launch {
            autoDetectRealLocation()
        }

        // 2. Periodic Live Update Coroutine (Every 60 seconds)
        viewModelScope.launch {
            while (isActive) {
                delay(60 * 1000L)
                if (_latitude.value != 0.0 && _longitude.value != 0.0) {
                    refreshWeather(autoNotify = false, silent = true)
                }
            }
        }
    }

    private suspend fun autoDetectRealLocation() {
        val detected = LocationHelper.detectBestLocation(getApplication())
        if (detected != null) {
            _latitude.value = detected.latitude
            _longitude.value = detected.longitude
            _currentCity.value = detected.displayName
            _isGpsActive.value = detected.isGps
        } else {
            // Default to global capital if all detection fails
            _currentCity.value = "London, UK"
            _latitude.value = 51.5074
            _longitude.value = -0.1278
            _isGpsActive.value = false
        }
        refreshWeather()
    }

    fun toggleMorphismStyle() {
        _morphismStyle.value = if (_morphismStyle.value == MorphismStyle.SOLID_MODERN) {
            MorphismStyle.LIQUID_GLASS
        } else {
            MorphismStyle.SOLID_MODERN
        }
    }

    fun setMorphismStyle(style: MorphismStyle) {
        _morphismStyle.value = style
    }

    fun refreshWeather(autoNotify: Boolean = false, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _isRefreshing.value = true
                if (_weatherState.value !is WeatherUiState.Success) {
                    _weatherState.value = WeatherUiState.Loading
                }
            }

            val result = repository.fetchWeather(_latitude.value, _longitude.value, _currentCity.value)
            result.fold(
                onSuccess = { resp ->
                    _weatherState.value = WeatherUiState.Success(resp)
                    val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                    _lastUpdatedTime.value = sdf.format(java.util.Date())

                    val currentTemp = resp.currentWeather?.temperature ?: 22.0
                    _liveTemperature.value = currentTemp
                    val weatherCode = resp.currentWeather?.weathercode ?: 0
                    val isDay = resp.currentWeather?.is_day ?: 1

                    // Update Dynamic Climate Adaptive Theme Color
                    _climateThemeColor.value = when {
                        currentTemp <= 5.0 -> FreezeGlacialBlue
                        currentTemp in 6.0..19.0 -> TemperateTeal
                        currentTemp in 20.0..28.0 -> RainDyApricot // User requested #f5a158
                        else -> ExtremeHeatOrange
                    }

                    // Update Moving Weather Art
                    _weatherArtType.value = when {
                        weatherCode in 71..77 -> WeatherArtType.SNOW
                        weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99) -> WeatherArtType.RAIN
                        isDay == 0 -> WeatherArtType.MOON
                        weatherCode in 1..3 || weatherCode in 45..48 -> WeatherArtType.CLOUD
                        else -> WeatherArtType.SUN
                    }

                    val rain = repository.calculateNextRainPrediction(resp)
                    val sun = repository.calculateNextSunnyPrediction(resp)
                    _nextRainPrediction.value = rain
                    _nextSunPrediction.value = sun
                    _activityRecommendations.value = repository.getOutdoorActivityRecommendations(resp)

                    // Auto-trigger bubble alert if user is signed in and rain predicted within 6h
                    if (isUserSignedIn && rain != null && rain.hoursFromNow <= 6) {
                        _activeBubbleAlert.value = rain
                        _isBubbleVisible.value = true

                        if (autoNotify) {
                            NotificationHelper.showClimateAlertNotification(
                                context = getApplication(),
                                title = rain.title,
                                predictionText = "${rain.formattedTime} • ${rain.probability}% confidence",
                                advisory = rain.advisory
                            )
                        }
                    }
                },
                onFailure = { err ->
                    if (!silent || _weatherState.value !is WeatherUiState.Success) {
                        _weatherState.value = WeatherUiState.Error(err.message ?: "Failed to fetch live weather data")
                    }
                }
            )
            _isRefreshing.value = false
        }
    }

    fun useCurrentLocation(context: Context) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            autoDetectRealLocation()
        }
    }

    fun selectCity(name: String, lat: Double, lon: Double) {
        _currentCity.value = name
        _latitude.value = lat
        _longitude.value = lon
        _isGpsActive.value = false
        _searchResults.value = emptyList()
        refreshWeather()
    }

    fun searchCities(query: String) {
        viewModelScope.launch {
            if (query.trim().length >= 2) {
                _searchResults.value = repository.searchCity(query.trim())
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

    fun triggerTestBubbleNotification(context: Context) {
        val testPrediction = _nextRainPrediction.value ?: NextWeatherEventPrediction(
            type = ClimateType.RAIN,
            title = "Rain Shower Alert",
            timeDescription = "In 2 hours",
            hoursFromNow = 2,
            probability = 85,
            advisory = "Precipitation incoming around 3:15 PM. Outdoor plans should shift to indoor spaces.",
            formattedTime = "In 2 hours (3:15 PM)"
        )

        _activeBubbleAlert.value = testPrediction
        _isBubbleVisible.value = true

        NotificationHelper.showClimateAlertNotification(
            context = context,
            title = testPrediction.title,
            predictionText = "${testPrediction.formattedTime} • ${testPrediction.probability}% confidence",
            advisory = testPrediction.advisory
        )
    }

    fun dismissBubble() {
        _isBubbleVisible.value = false
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun signInWithGoogle() {
        _userProfile.value = UserProfile(
            displayName = "Fortune Adeola",
            email = "adeolafortuneadeola12345@gmail.com",
            isGoogleSignedIn = true,
            avatarInitials = "FA"
        )
    }

    fun signOut() {
        _userProfile.value = UserProfile(
            displayName = "Guest User",
            email = "",
            isGoogleSignedIn = false,
            avatarInitials = "GU"
        )
    }

    fun toggleGoogleSignIn(context: Context? = null) {
        if (!_userProfile.value.isGoogleSignedIn) {
            signInWithGoogle()
            if (context != null) {
                useCurrentLocation(context)
            }
        } else {
            signOut()
        }
    }

    fun updatePrefs(newPrefs: NotificationPreferences) {
        _notificationPrefs.value = newPrefs
    }

    fun addTask(
        title: String,
        description: String,
        category: String,
        climateAffinity: String,
        day: String,
        time: String,
        priority: String
    ) {
        viewModelScope.launch {
            val entity = WeatherTaskEntity(
                title = title,
                description = description,
                category = category,
                preferredClimate = climateAffinity,
                scheduledDay = day,
                scheduledTime = time,
                priority = priority
            )
            repository.insertTask(entity)
        }
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteTask(task: WeatherTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun syncRecommendationToCalendar(context: Context, rec: OutdoorActivityRecommendation): Boolean {
        return CalendarHelper.addWeatherEventToCalendar(
            context = context,
            title = rec.activityName,
            description = "Weather Match: ${rec.weatherSuitability} (${rec.reason}). Planned for ${rec.bestTimeWindow}",
            location = _currentCity.value,
            startHourOffset = rec.startHourOffset,
            durationHours = rec.durationHours
        )
    }

    private suspend fun seedInitialTasksIfEmpty() {
        val initial = listOf(
            WeatherTaskEntity(
                title = "Morning Coastal Jog",
                description = "4km run along ocean boulevard while skies are clear",
                category = "Fitness",
                preferredClimate = "SUNNY",
                scheduledDay = "Today",
                scheduledTime = "8:00 AM",
                priority = "High"
            ),
            WeatherTaskEntity(
                title = "Car Exterior Wash & Wax",
                description = "Apply rain-repellent coat before next rainfall",
                category = "Chores",
                preferredClimate = "SUNNY",
                scheduledDay = "Today",
                scheduledTime = "2:00 PM",
                priority = "Normal"
            ),
            WeatherTaskEntity(
                title = "Indoor Mobility & Core Workout",
                description = "Backup indoor training in case rain sets in early",
                category = "Fitness",
                preferredClimate = "INDOOR",
                scheduledDay = "Today",
                scheduledTime = "5:30 PM",
                priority = "Normal"
            ),
            WeatherTaskEntity(
                title = "Inspect Solar Panel Efficiency",
                description = "Compare clear sky solar irradiance vs cloudy days",
                category = "Sustainability",
                preferredClimate = "SUNNY",
                scheduledDay = "Tomorrow",
                scheduledTime = "11:00 AM",
                priority = "Low"
            )
        )
        for (item in initial) {
            repository.insertTask(item)
        }
    }
}
