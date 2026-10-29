package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherTaskDao {
    @Query("SELECT * FROM weather_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<WeatherTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: WeatherTaskEntity): Long

    @Update
    suspend fun updateTask(task: WeatherTaskEntity)

    @Delete
    suspend fun deleteTask(task: WeatherTaskEntity)

    @Query("DELETE FROM weather_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("UPDATE weather_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)

    @Query("SELECT * FROM cached_weather WHERE locationKey = :key LIMIT 1")
    fun getCachedWeather(key: String = "default_location"): Flow<CachedWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheWeather(cached: CachedWeatherEntity)
}
