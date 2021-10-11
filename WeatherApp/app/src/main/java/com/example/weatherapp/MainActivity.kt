package com.example.weatherapp

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import com.android.volley.*

class MainActivity : AppCompatActivity() {

    val api: String = "06c921750b9a82d8f5d1294e1586276f"
    var weatherUrl: String = ""
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        var a = viewModel.getCapitals()



    }

    fun loadSwitch() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        var auto = sharedPrefs.getBoolean("auto", true)

        if (auto) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1000)
                return
            } else {
                getLocationUpdate()
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProvider.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
        } else {
            fab.show()
            loadCity()
            fab.setOnClickListener {
                showDialog()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)

        if (menu != null) {
            for (i in 0 until menu.size()) {
                var menuItem = menu.getItem(i)
                var spannable = SpannableString(menuItem.title.toString())
                spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, spannable.length, 0)
                menuItem.title = spannable
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }

    public fun saveCity(city: String) {
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val editor = sharedPrefs.edit().apply() {
            putString("City", city)
            apply()
        }
    }


    fun loadCity() {
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedCity = sharedPrefs.getString("City", null)
        weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${savedCity}&units=metric&appid=$api"
        getWeather()
    }



    fun showDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Enter a city name")

        val input = EditText(this).apply {
            hint = "City"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
            try {
                var city = input.text.toString().trim()
                if (city != null && city != "") {
                    weatherUrl =
                        "https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=$api"
                    getWeather()
                    saveCity(city)
                }
            } catch (ex: Exception) {
                Toast.makeText(applicationContext, "Enter correct city", Toast.LENGTH_SHORT).show()
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }


    private fun getLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1000)
            return
        } else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                locationRequest = LocationRequest().apply {
                    interval = 50000
                    fastestInterval = 50000
                    smallestDisplacement = 170f
                    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: showAlertLocation()
                        if (locationResult!!.locations.isNotEmpty()) {
                            val addresses: List<Address>?
                            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                            addresses = geoCoder.getFromLocation(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude,
                                1
                            )
                            if (addresses != null && addresses.isNotEmpty()) {
                                val city = addresses[0].locality
                                Log.e(
                                    "location",
                                    "$city "
                                )
                                weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=$api"
                                getWeather()
                                saveCity(city)
                            }

                        }
                    }
                }
            } else {
                showAlertLocation()
            }

        }
    }

    fun getWeather() {
        val queue = Volley.newRequestQueue(this)
        val url: String = weatherUrl
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                Log.e("lat", response.toString())
                val jsonObj = JSONObject(response)

                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure") + " hPa"
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " m/s"
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")
                val humidity = main.getString("humidity") + "%"

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

            },
            {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)

    }


    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setMessage("Your location settings is set to off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    override fun onResume() {
        super.onResume()

        try {
            Toast.makeText(applicationContext, "Please wait...", Toast.LENGTH_SHORT).show()
            fab.hide()
            loadSwitch()
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
        }
    }
}





