package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeoLocation
import com.example.ui.RainDyViewModel
import com.example.ui.components.MorphismStyle
import com.example.ui.components.popOnTap
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.MetricPillDark
import com.example.ui.theme.MetricPillLight
import com.example.ui.theme.RainDyApricot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    viewModel: RainDyViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val isGpsActive by viewModel.isGpsActive.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var countryInput by remember { mutableStateOf("") }
    var stateInput by remember { mutableStateOf("") }
    var townInput by remember { mutableStateOf("") }
    var singleSearchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<GeoLocation?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        viewModel.useCurrentLocation(context)
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x33000000))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(climateColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = climateColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Location & Map Radar",
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Find exact coordinates & live weather",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Current Live Location Auto-Detector Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isGpsActive) AuroraEmerald.copy(alpha = 0.12f) else (if (isDark) MetricPillDark else MetricPillLight))
                    .border(
                        width = 1.dp,
                        color = if (isGpsActive) AuroraEmerald.copy(alpha = 0.4f) else (if (isDark) Color(0x22FFFFFF) else Color(0x1F000000)),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .popOnTap {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .padding(16.dp)
                    .testTag("use_live_location_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AuroraEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = AuroraEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Use My Real Location",
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (isGpsActive) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AuroraEmerald)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Current: $currentCity",
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text(
                        text = "Detect 📍",
                        color = AuroraEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Change Location: Country, State, Town Search
            Text(
                text = "CHANGE LOCATION (COUNTRY, STATE, TOWN)",
                color = climateColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fast worldwide search field
            OutlinedTextField(
                value = singleSearchQuery,
                onValueChange = {
                    singleSearchQuery = it
                    if (it.length >= 2) {
                        viewModel.searchCities(it)
                    }
                },
                placeholder = { Text("Search town, state, or country worldwide...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = climateColor
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = climateColor,
                    unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x33CBD5E1),
                    focusedContainerColor = if (isDark) Color(0x11FFFFFF) else Color(0x08000000),
                    unfocusedContainerColor = if (isDark) Color(0x0AFFFFFF) else Color(0x04000000)
                ),
                singleLine = true
            )

            // Or manual Country, State, Town Fields
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = townInput,
                    onValueChange = {
                        townInput = it
                        val query = listOf(it, stateInput, countryInput).filter { s -> s.isNotBlank() }.joinToString(", ")
                        if (query.length >= 2) viewModel.searchCities(query)
                    },
                    placeholder = { Text("Town/City", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = climateColor,
                        unfocusedBorderColor = if (isDark) Color(0x22FFFFFF) else Color(0x22CBD5E1)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stateInput,
                    onValueChange = {
                        stateInput = it
                        val query = listOf(townInput, it, countryInput).filter { s -> s.isNotBlank() }.joinToString(", ")
                        if (query.length >= 2) viewModel.searchCities(query)
                    },
                    placeholder = { Text("State", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = climateColor,
                        unfocusedBorderColor = if (isDark) Color(0x22FFFFFF) else Color(0x22CBD5E1)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = countryInput,
                    onValueChange = {
                        countryInput = it
                        val query = listOf(townInput, stateInput, it).filter { s -> s.isNotBlank() }.joinToString(", ")
                        if (query.length >= 2) viewModel.searchCities(query)
                    },
                    placeholder = { Text("Country", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = climateColor,
                        unfocusedBorderColor = if (isDark) Color(0x22FFFFFF) else Color(0x22CBD5E1)
                    ),
                    singleLine = true
                )
            }

            // Search Results dropdown list
            if (searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF162032) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0x22FFFFFF) else Color(0x22CBD5E1), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "SELECT MATCHING MAP COORDINATES:",
                        color = climateColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    searchResults.take(5).forEach { loc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .popOnTap {
                                    selectedLocation = loc
                                }
                                .background(if (selectedLocation?.id == loc.id) climateColor.copy(alpha = 0.2f) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = loc.name,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${loc.adminRegion ?: ""}, ${loc.country ?: ""} • (${String.format("%.2f", loc.latitude)}, ${String.format("%.2f", loc.longitude)})",
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = if (selectedLocation?.id == loc.id) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (selectedLocation?.id == loc.id) AuroraEmerald else climateColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 3. Google Maps / Radar Pinpoint Preview Card
            selectedLocation?.let { loc ->
                Spacer(modifier = Modifier.height(16.dp))
                GoogleMapRadarPreviewCard(
                    location = loc,
                    isDark = isDark,
                    accentColor = climateColor,
                    onConfirm = {
                        val fullName = buildString {
                            append(loc.name)
                            if (!loc.country.isNullOrBlank()) append(", ${loc.country}")
                        }
                        viewModel.selectCity(fullName, loc.latitude, loc.longitude)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Quick Worldwide Cities
            Text(
                text = "POPULAR GLOBAL REGIONS",
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val globalPresets = listOf(
                "Lagos, Nigeria" to Pair(6.5244, 3.3792),
                "London, UK" to Pair(51.5074, -0.1278),
                "New York, USA" to Pair(40.7128, -74.0060),
                "Tokyo, Japan" to Pair(35.6762, 139.6503),
                "Paris, France" to Pair(48.8566, 2.3522),
                "Dubai, UAE" to Pair(25.2048, 55.2708),
                "Toronto, Canada" to Pair(43.6532, -79.3832),
                "Sydney, Australia" to Pair(-33.8688, 151.2093),
                "Nairobi, Kenya" to Pair(-1.2921, 36.8219),
                "Berlin, Germany" to Pair(52.5200, 13.4050),
                "São Paulo, Brazil" to Pair(-23.5505, -46.6333)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                globalPresets.forEach { (name, coords) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) MetricPillDark else MetricPillLight)
                            .border(0.5.dp, if (isDark) Color(0x22FFFFFF) else Color(0x1F000000), RoundedCornerShape(16.dp))
                            .popOnTap {
                                viewModel.selectCity(name, coords.first, coords.second)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = name,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/**
 * Aesthetic Google Maps / Radar Preview Card with Canvas coordinates pinpoint
 */
@Composable
fun GoogleMapRadarPreviewCard(
    location: GeoLocation,
    isDark: Boolean,
    accentColor: Color,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDark) {
                    Brush.verticalGradient(listOf(Color(0xFF131D31), Color(0xFF0B1220)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC)))
                }
            )
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Google Map Radar Verification",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "GPS LOCK ✓",
                color = AuroraEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stylized Canvas Map Radar with Target Pinpoint
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF0A0F1D) else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Grid topographic lines
                for (i in 1..4) {
                    val y = h * (i / 5f)
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }
                for (i in 1..6) {
                    val x = w * (i / 7f)
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                }

                // Radar rings around target center
                val center = Offset(w / 2f, h / 2f)
                drawCircle(
                    color = accentColor.copy(alpha = 0.15f),
                    radius = 38.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.35f),
                    radius = 22.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = accentColor,
                    radius = 5.dp.toPx(),
                    center = center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "📍 ${location.name}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lat: ${String.format("%.4f", location.latitude)}° • Lon: ${String.format("%.4f", location.longitude)}°",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedButton(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .popOnTap { onConfirm() }
                .testTag("confirm_location_selection_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = accentColor,
                contentColor = Color(0xFF0F172A)
            )
        ) {
            Text(
                text = "Confirm & Load Weather for ${location.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
