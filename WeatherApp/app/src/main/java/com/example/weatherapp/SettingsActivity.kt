package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.preference.SwitchPreference
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var a = prefs.getBoolean("auto", false)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            var switch: SwitchPreferenceCompat? = findPreference<SwitchPreferenceCompat>(R.id.Switch.toString())
            //button.text = button.isEnabled.toString()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}