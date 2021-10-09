package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.db.WeatherEntity
import com.example.weatherapp.db.WeatherViewModel

class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    var cities = listOf("Minsk", "London", "Moscow", "Washington", "Tirana", "Baku", "Vienna", "Paris", "Brussels",
        "Sofia", "Riga", "Vaduz", "Vilnius", "Luxembourg", "Valletta", "Monaco", "Amsterdam", "Oslo", "Warsaw", "Lisbon",
    "Bucharest", "San Marino", "Bratislava", "Ankara", "Kiev", "Vatican", "Stockholm", "Madrid", "Belgrade", "Bern", "Ljubljana",
    "Skopje", "Podgorica", "Chisinau", "Vaduz", "Yerevan", "Andorra la Vella", "Sarajevo", "Prague", "Tallinn", "Helsinki",
    "Tbilisi", "Berlin", "Athens", "Budapest", "Reykjavik", "Rome", "Pristina", "Copenhagen", "Zagreb", "Nicosia", "Nur-Sultan")
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities.sorted())
        listView.adapter = adapter
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
               val value = adapter.getItem(position)
                searchView.setQuery(value, false)
                listView.visibility = View.GONE
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                listView.visibility = View.VISIBLE
                adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(text: String): Boolean {
                if (cities.contains(text)) {
                    var sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().apply {
                        putString("City", text)
                        apply()
                    }
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Enter the correct city name", Toast.LENGTH_SHORT).show()
                }
                return false
            }
        })
    }

}

