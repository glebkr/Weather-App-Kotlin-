package com.example.weatherapp

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.Gravity.apply
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.util.Strings
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.android.volley.*

class MainActivity : AppCompatActivity() {
    //var city: String? = null
    val api: String = "06c921750b9a82d8f5d1294e1586276f"
    var weatherUrl: String = ""
    lateinit var fusedLocationProvider: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    /*
    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val location: Location? = locationResult?.lastLocation
            if (location != null) {
                val addresses: List<Address>?
                val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                addresses = geoCoder.getFromLocation(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude,
                    1
                )
                if (addresses != null && addresses.isNotEmpty()) {
                    val cit = addresses[0].locality
                    Log.e(
                        "location",
                        "$cit "
                    )
                }

            }
        }
    }

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
            fab.setOnClickListener {
                showDialog()
            }
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_LONG).show()
        }

    }

    fun showDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("Enter a city name")

        val input = EditText(this).apply {
            setHint("City")
            inputType = InputType.TYPE_CLASS_TEXT
        }
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
            var city = input.text.toString().trim()
            weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=$api"
            getWeather()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }


    private fun getLocation() {

        try {
            Log.e("lat", "function")
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
                    fusedLocationProvider.lastLocation.addOnCompleteListener(this) { task ->
                        val location: Location? = task.result
                        if (location == null)
                            getLocationUpdate()
                        else {
                            val addresses: List<Address>?
                            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                            addresses = geoCoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            if (addresses != null && addresses.isNotEmpty()) {
                                val cit = addresses[0].locality
                                Log.e(
                                    "location",
                                    "$cit "
                                )
                                weatherUrl =
                                    "https://api.openweathermap.org/data/2.5/weather?q=${cit}&units=metric&appid=$api"
                                getWeather()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_SHORT).show()
        }

                /*
                val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 10)
                val cit = addresses[0].locality
                weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${cit}&units=metric&appid=$api"
                getWeather()

                 */

                /*
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProvider.requestLocationUpdates()
                fusedLocationProvider.lastLocation.addOnSuccessListener {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 10)
                    val city = addresses[0].locality

                    weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=$api"
                    Log.e("lat", weatherUrl.toString())

                    getWeather()

                }

                      */
                /*
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                locationRequest = LocationRequest()
                locationRequest.interval = 50000
                locationRequest.fastestInterval = 50000
                locationRequest.smallestDisplacement = 170f
                locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        if (locationResult.locations.isNotEmpty()) {
                            val addresses: List<Address>?
                            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                            addresses = geoCoder.getFromLocation(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude,
                                1
                            )
                            if (addresses != null && addresses.isNotEmpty()) {
                                val cit = addresses[0].locality
                                Log.e(
                                    "location",
                                    "$cit "
                                )
                                weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=${cit}&units=metric&appid=$api"
                                Toast.makeText(applicationContext, weatherUrl, Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                           */


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
                                Toast.makeText(applicationContext, weatherUrl, Toast.LENGTH_SHORT).show()
                                getWeather()
                            }

                        }
                    }
                }
            } else {
                showAlertLocation()
            }

        }
    }

    private fun getWeather() {
        val queue = Volley.newRequestQueue(this)
        val url: String = weatherUrl
        Toast.makeText(applicationContext, weatherUrl, Toast.LENGTH_SHORT).show()
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
        val dialog = AlertDialog.Builder(this)
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


/*
    private fun requestLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 0
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates = 1
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
            getLocation()
        }

    }





    private fun getCoordinates() {
        var city: String? = null

        try {
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
                val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                city = getCity(location!!.latitude, location!!.longitude)
                getForecast(city)
            }
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_LONG).show()
        }

    }


    private fun getCity(lat: Double, lon: Double): String? {
        var city: String? = null
        val addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(lat, lon, 10)
            if (addresses != null && addresses[0].locality != null) {
                city = addresses[0].locality
                }
            }
        catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_SHORT).show()
        }
        return city

    }





    private fun getForecast(city: String?) : String? {
        var url: String? = null
        try {
            url = "https://api.openweathermap.org/data/2.5/weather?q={$city}&units=metric&appid=$api"
            //getCurrentWeatherFromJsonResponse(url)
            weatherTask().execute(url)
        } catch (ex: Exception){
            Toast.makeText(applicationContext, ex.toString(), Toast.LENGTH_SHORT).show()
        }
        return url
    }


    private fun getCurrentWeatherFromJsonResponse(result: String?) {
        val jsonObj = JSONObject(result)

        val main = jsonObj.getJSONObject("main")
        val sys = jsonObj.getJSONObject("sys")
        val wind = jsonObj.getJSONObject("wind")
        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

        val updatedAt:Long = jsonObj.getLong("dt")
        val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
        val temp = main.getString("temp") + "°C"
        val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
        val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
        val pressure = main.getString("pressure") + " hPa"
        val sunrise:Long = sys.getLong("sunrise")
        val sunset:Long = sys.getLong("sunset")
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
        findViewById<TextView>(R.id.temp_min).text = "Min Temp: 11.35°C"
        findViewById<TextView>(R.id.temp_max).text = "Max Temp: 12.06°C"
        findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
        findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
        findViewById<TextView>(R.id.wind).text = windSpeed
        findViewById<TextView>(R.id.pressure).text = pressure
        findViewById<TextView>(R.id.humidity).text = humidity
    }



/*
    private fun checkLocation() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdate()
    }



    fun getLocationUpdate() {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest()
            locationRequest.interval = 50000
            locationRequest.fastestInterval = 50000
            locationRequest.smallestDisplacement = 170f
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    if (locationResult.locations.isNotEmpty()) {
                        val addresses: List<Address>?
                        val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                        addresses = geoCoder.getFromLocation(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            1
                        )
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address: String? = addresses[0].getAddressLine(0)
                            val cit = addresses[0].locality
                            val state: String? = addresses[0].adminArea
                            val country: String? = addresses[0].countryName
                            val postalCode: String? = addresses[0].postalCode
                            val knownName: String? = addresses[0].featureName

                            Log.e(
                                "location",
                                "$address $cit $state $postalCode $country $knownName"
                            )
                        }
                    }
                }
            }

    }

*/


    inner class weatherTask() : AsyncTask<String, Void, String>()
    {

        override fun onPreExecute()
        {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?) : String?
        {
            var response: String? = null
            try
            {
                //response = URL("https://api.openweathermap.org/data/2.5/weather?q=Minsk&units=metric&appid=$api").readText(Charsets.UTF_8)
                response = URL(p0[0]).readText(Charsets.UTF_8)
            } catch (ex: Exception)
            {

            }
            return response
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try
            {
                val jsonObj = JSONObject(result)

                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure") + " hPa"
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
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
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            }
            catch (ex: Exception)
            {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
            }


        }

    }



 */

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),101)
            return
        }
        getLocationUpdate()
        fusedLocationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }


    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

}










