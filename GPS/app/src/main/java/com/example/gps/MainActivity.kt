package com.example.gps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 98

class MainActivity : AppCompatActivity() {
    private lateinit var locationTextView: TextView
    private lateinit var saveLocationButton: Button
    private lateinit var historyButton: Button
    private lateinit var mapButton: Button

    private lateinit var mLocationManager: LocationManager
    private var mLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val mainView = findViewById<View>(R.id.main)

        locationTextView = findViewById(R.id.locationTextView)
        saveLocationButton = findViewById(R.id.locationSaveButton)
        historyButton = findViewById(R.id.locationHistoryButton)
        mapButton = findViewById(R.id.mapButton)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        runApp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Permission granted")
                    // Permission granted, continue with app execution
                    runApp()
                } else {
                    Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Permission denied")
                }
            }
        }
    }

    private fun runApp() {

        if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Laita sijaintitiedot päälle käytääksesi sovellusta",
                Toast.LENGTH_LONG).show()
        }

        // Tarkastetaan luvat
        try {
            if (askPermission(this)) {
                // Haetaan ja päivitetään viimeisin tunnettu sijainti.
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                mLocation?.let { location ->
                    updateLocationTextView(location)
                }

                // Asetetaan LocationManager pyytämään uusi sijainti 3 sekunnin välein ja kun
                // sijainti muuttuu vähintään 10m. Jos laitteen API level on
                // 31 tai yli käytetään tarkempaa sijantitiedon tarjoajaa
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.FUSED_PROVIDER,
                        3000,
                        10f
                    ) { location ->
                        // Päivitetään koordinaatit kun sijainti muuttuu
                        mLocation = location
                        updateLocationTextView(location)
                        Log.d(TAG, "Using: LocationManager.FUSED_PROVIDER" )
                    }
                } else {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        3000,
                        10f
                    ) { location ->
                        // Päivitetään koordinaatit kun sijainti muuttuu
                        mLocation = location
                        updateLocationTextView(location)
                        Log.d(TAG, "Using: LocationManager.NETWORK_PROVIDER" )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Error: App doesn't have permission to access location")
        }

        saveLocationButton.setOnClickListener {
            mLocation?.let { location ->
                val isSuccess = SharedPreferences(application).saveLocation(location)
                if (isSuccess) {
                    Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Location already saved", Toast.LENGTH_SHORT).show()
                }
            }
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, SavedLocationsActivity::class.java)
            startActivity(intent)
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, MapViewActivity::class.java)
            startActivity(intent)
        }

    }

    // Päivittää uudet koordinaatit tekstinäkymään
    private fun updateLocationTextView(location: Location) {
        locationTextView.text = getString(
            R.string.lat_lon,
            location.latitude.toString(),
            location.longitude.toString()
        )
    }
}
