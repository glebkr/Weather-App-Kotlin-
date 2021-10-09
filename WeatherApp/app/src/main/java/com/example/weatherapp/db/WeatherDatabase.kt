package com.example.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherEntity::class],
    version = 3,
    exportSchema = true
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getWeatherDao() : WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context) : WeatherDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                WeatherDatabase::class.java, "WeatherDB.db")
                    .createFromAsset("database/Capitals.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}