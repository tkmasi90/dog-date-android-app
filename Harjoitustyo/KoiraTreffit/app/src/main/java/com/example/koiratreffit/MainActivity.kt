package com.example.koiratreffit

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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "MainActivity"
private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 98

class MainActivity : AppCompatActivity() {
    private lateinit var mLocationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkUserAuthentication()
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

    private fun checkUserAuthentication() {
        val auth = Firebase.auth

        if (auth.currentUser == null) {
            redirectToSignInActivity()
        } else {
            runApp()
        }
    }

    private fun redirectToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun runApp() {
        if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(
                this, "Laita sijaintitiedot päälle käytääksesi sovellusta",
                Toast.LENGTH_LONG
            ).show()
        }

        // Tarkastetaan luvat
        try {
            if (askPermission(this)) {
                val intent = Intent(this, MapViewActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Error: App doesn't have permission to access location")
        }
    }
}