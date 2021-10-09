package com.example.weatherapp.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : WeatherRepository
    init {
        val weatherDao = WeatherDatabase.getDatabase(application).getWeatherDao()
        repository = WeatherRepository(weatherDao)
    }

    fun upsert(item : WeatherEntity) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.upsert(item)
        }
    }

    fun getCapitals() {
        viewModelScope.launch (Dispatchers.IO) {
            repository.getCapitals()
        }
    }
}