package com.example.weatherapp.db

class WeatherRepository(private val weatherDao: WeatherDao) {
    suspend fun getCapitals() = weatherDao.getCapitals()
    suspend fun upsert(item: WeatherEntity) = weatherDao.upsert(item)
}