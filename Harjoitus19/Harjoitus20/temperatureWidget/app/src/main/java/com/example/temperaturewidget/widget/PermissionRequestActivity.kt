package com.example.temperaturewidget.widget

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Activity joka ajetetaan kun widget tarvitsee käyttäjältä oikeudet sijaintitietojen käyttöön
class PermissionRequestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            sendRefreshBroadcast()
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            sendRefreshBroadcast()
            finish()
        }
    }

    private fun sendRefreshBroadcast() {
        sendBroadcast(Intent(this, WidgetRefreshReceiver::class.java))
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
