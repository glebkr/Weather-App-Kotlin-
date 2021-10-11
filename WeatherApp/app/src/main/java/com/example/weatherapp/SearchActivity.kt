package com.example.weatherapp

import androidx.lifecycle.Observer
import android.content.Intent
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.contains
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.db.WeatherEntity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.view.*


class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    var cities = listOf("Minsk", "London", "Moscow", "Washington", "Tirana", "Baku", "Vienna", "Paris", "Brussels",
        "Sofia", "Riga", "Vaduz", "Vilnius", "Luxembourg", "Valletta", "Monaco", "Amsterdam", "Oslo", "Warsaw", "Lisbon",
    "Bucharest", "San Marino", "Bratislava", "Ankara", "Kiev", "Vatican", "Stockholm", "Madrid", "Belgrade", "Bern", "Ljubljana",
    "Skopje", "Podgorica", "Chisinau", "Vaduz", "Yerevan", "Andorra la Vella", "Sarajevo", "Prague", "Tallinn", "Helsinki",
    "Tbilisi", "Berlin", "Athens", "Budapest", "Reykjavik", "Rome", "Pristina", "Copenhagen", "Zagreb", "Nicosia", "Nur-Sultan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        val adapter = CapitalsListAdapter(listOf())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        viewModel.getCapitals().observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.rv)


        adapter.setOnItemClickListener(object : CapitalsListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                searchView.setQuery(adapter.items[position].capital,false)
                recyclerView.visibility = View.GONE
            }
        }
        )
        /*
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
         */

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                  rv.visibility = View.VISIBLE
             //   adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(text: String): Boolean {
                var list : MutableList<String> = mutableListOf()
                for (item in adapter.items) {
                    list.add(item.capital)
                }
                if (list.contains(text)) {
                    var sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
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

