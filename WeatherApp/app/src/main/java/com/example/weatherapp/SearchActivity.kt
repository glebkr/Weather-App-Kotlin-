package com.example.weatherapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView

class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    var cities = listOf("Minsk", "London", "Moscow", "Washington")
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
               var value = adapter.getItem(position)
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

            override fun onQueryTextSubmit(text: String?): Boolean {
                if (cities.contains(text)) {
                    var sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().apply {
                        putString("City", text)
                        apply()
                    }
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
                return false
            }
        })
    }

}

