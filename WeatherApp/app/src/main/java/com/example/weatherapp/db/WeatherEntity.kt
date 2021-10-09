package com.example.weatherapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Capitals_list")
data class  WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val country: String,
    val capital: String
    )

