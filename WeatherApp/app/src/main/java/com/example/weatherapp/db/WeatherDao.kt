package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun upsert(item : WeatherEntity)
    @Query("SELECT * FROM Capitals_list")
    fun getCapitals() : List<WeatherEntity>
}