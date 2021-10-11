package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.db.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherDao = WeatherDatabase.getDatabase(application).getWeatherDao()
    fun getCapitals() = weatherDao.getCapitals()

}