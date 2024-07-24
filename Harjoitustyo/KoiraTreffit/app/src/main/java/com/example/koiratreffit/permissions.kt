package com.example.koiratreffit

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

private const val TAG = "Permissions"
private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 98

// Tarkistetaan ja pyydetään tarvittavat luvat
fun askPermission(context: Context): Boolean {
    Log.d(TAG, "askPermission()")

    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasInternetPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.INTERNET
    ) == PackageManager.PERMISSION_GRANTED

    return if (hasFineLocationPermission && hasCoarseLocationPermission && hasInternetPermission) {
        Log.d(TAG, "Permission is granted")
        true
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
        false
    }
}