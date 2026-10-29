package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.WeatherTaskEntity
import com.example.ui.RainDyViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MorphismStyle
import com.example.ui.components.WeeklyGoalProgressDashboard
import com.example.ui.components.popOnTap
import com.example.ui.theme.AlertRose
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun TasksScreen(
    viewModel: RainDyViewModel,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredTasks = when (selectedFilter) {
        "Sunny" -> tasks.filter { it.preferredClimate == "SUNNY" }
        "Indoor" -> tasks.filter { it.preferredClimate == "INDOOR" }
        "Active" -> tasks.filter { !it.isCompleted }
        "Done" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(climateColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = "Task Manager",
                                tint = climateColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Weather-Themed Planner",
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Offline-first SQLite storage • Auto-syncs",
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Minimalist Weekly Goals Progress Dashboard
            item {
                WeeklyGoalProgressDashboard(
                    completedTasks = completedCount,
                    totalTasks = totalCount,
                    outdoorGoalsDone = 5,
                    outdoorGoalsTarget = 7,
                    isDark = isDark
                )
            }

            // Filter Tabs Carousel
            item {
                val filters = listOf("All", "Active", "Sunny", "Indoor", "Done")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) {
                                        climateColor
                                    } else {
                                        if (isDark) Color(0x331E293B) else Color(0x55E2E8F0)
                                    }
                                )
                                .border(
                                    0.5.dp,
                                    if (isSelected) Color.Transparent else Color(0x33FFFFFF),
                                    RoundedCornerShape(20.dp)
                                )
                                .popOnTap { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) {
                                    Color(0xFF0F172A)
                                } else {
                                    if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🌤️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No tasks found for \"$selectedFilter\"",
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add a climate-aligned task with the + button below",
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        isDark = isDark,
                        morphismStyle = morphismStyle,
                        climateColor = climateColor,
                        onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add New Task with Pop on Tap
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = climateColor,
            contentColor = Color(0xFF0F172A),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .popOnTap { showAddDialog = true }
                .testTag("add_task_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }

        if (showAddDialog) {
            AddTaskDialog(
                isDark = isDark,
                climateColor = climateColor,
                onDismiss = { showAddDialog = false },
                onAdd = { title, desc, cat, clim, day, time, prio ->
                    viewModel.addTask(title, desc, cat, clim, day, time, prio)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskItemCard(
    task: WeatherTaskEntity,
    isDark: Boolean,
    morphismStyle: MorphismStyle,
    climateColor: Color,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val climateIcon = when (task.preferredClimate) {
        "SUNNY" -> "☀️ Sunny Required"
        "INDOOR" -> "🌧️ Indoor / Rain Backup"
        "DRY" -> "⛅ Dry Weather"
        else -> "🌈 Flexible"
    }

    val itemColor = when (task.preferredClimate) {
        "SUNNY" -> climateColor
        "INDOOR" -> RainBlueElectric
        "DRY" -> AuroraEmerald
        else -> Color(0xFFC084FC)
    }

    LiquidGlassCard(
        isDark = isDark,
        morphismStyle = morphismStyle,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onToggle(!task.isCompleted) },
                modifier = Modifier
                    .size(36.dp)
                    .popOnTap { onToggle(!task.isCompleted) }
                    .testTag("task_toggle_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Completed" else "Not completed",
                    tint = if (task.isCompleted) AuroraEmerald else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) {
                        if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                    } else {
                        if (isDark) Color.White else Color(0xFF0F172A)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(itemColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = climateIcon,
                            color = itemColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "• ${task.scheduledDay} ${task.scheduledTime}",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(32.dp)
                    .popOnTap { onDelete() }
                    .testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    isDark: Boolean,
    climateColor: Color,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Outdoor") }
    var climateAffinity by remember { mutableStateOf("SUNNY") }
    var day by remember { mutableStateOf("Today") }
    var time by remember { mutableStateOf("10:00 AM") }
    var priority by remember { mutableStateOf("Normal") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(if (isDark) Color(0xFF0F172A) else Color.White)
                .border(1.dp, if (isDark) Color(0x33FFFFFF) else Color(0x22CBD5E1), RoundedCornerShape(28.dp))
                .padding(24.dp)
                .testTag("add_task_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "New Weather-Smart Task",
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g. Lawn Mowing, Car Wash") },
                    modifier = Modifier.fillMaxWidth().testTag("new_task_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = climateColor)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = climateColor)
                )

                Text(
                    text = "Optimal Weather Condition",
                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val climates = listOf("SUNNY" to "☀️ Sunny", "INDOOR" to "🌧️ Indoor", "ANY" to "🌈 Any")
                    climates.forEach { (key, label) ->
                        val selected = climateAffinity == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) climateColor else (if (isDark) Color(0x331E293B) else Color(0x55E2E8F0)))
                                .popOnTap { climateAffinity = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color(0xFF0F172A) else (if (isDark) Color.White else Color(0xFF0F172A)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ElevatedButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(title, description, category, climateAffinity, day, time, priority)
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = climateColor,
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .popOnTap {
                                if (title.isNotBlank()) {
                                    onAdd(title, description, category, climateAffinity, day, time, priority)
                                }
                            }
                            .testTag("save_new_task_btn")
                    ) {
                        Text("Save Offline", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
