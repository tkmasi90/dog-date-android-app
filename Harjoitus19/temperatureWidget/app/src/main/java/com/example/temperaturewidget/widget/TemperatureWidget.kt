package com.example.temperaturewidget.widget

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.core.content.ContextCompat
import androidx.glance.Button
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.android.volley.RequestQueue
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "TemperatureWidgetTAG"
lateinit var requestQueue: RequestQueue

class TemperatureWidget : GlanceAppWidget() {
    // FusedLocationProviderClient - Pääluokka sijaintipäivitysten vastaanottamista varten.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Tallettaa tiedon nykyisestä sijainnista
    private var currentLocation: MutableState<Location?> = mutableStateOf(null)

    // Overridataan provideGlance widgetin sisällön asettamiseksi
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            ContentView(context)
        }
    }

    @Composable
    private fun ContentView(context: Context) {

        // State joka säilyttää lämpötilatiedot
        val temperatures = rememberSaveable(stateSaver = WeatherItemSaver) { mutableStateOf(WeatherItem("", emptyList(), emptyList())) }
        val coroutineScope = rememberCoroutineScope()

        Column(modifier = GlanceModifier.fillMaxSize()
            .background(ColorProvider(MaterialTheme.colorScheme.background))
            .cornerRadius(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            UpdateLocationButton(context)
            if (currentLocation.value != null) {
                DisplayTemperature(context, currentLocation.value!!, temperatures, coroutineScope)
            }
            // Pyydetään lupaa tarvittessa
            else {
                if (!hasLocationPermission(context)) {
                    RequestPermissionButton(context)
                }
            }
        }
    }

    // Funktio joka tarkistaa, onko tarvittavat sijaintiluvat myönnetty
    private fun hasLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.INTERNET,
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // Painike nykyisen sijainnin hakemiseen ja lämpötilaennusteen päivittämiseen
    @Composable
    private fun UpdateLocationButton(context: Context) {
        Button(modifier = GlanceModifier.fillMaxWidth()
            .cornerRadius(10.dp),
            text = "Get current location temperature forecast",
            style = TextStyle(textDecoration = TextDecoration.Underline),
            onClick = {
                fetchLocation(context)
            }
        )
    }

    // Haetaan nykyinen sijainti ja talletetaan se currentLocationin valueksi
    private fun fetchLocation(context: Context) {
        Log.d(TAG, "Requesting location updates")
        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        Log.d(TAG, "Last known location received: $it")
                        currentLocation.value = it // Update current location state
                    } ?: run {
                        Log.d(TAG, "No last known location available")
                    }
                }
                .addOnFailureListener { e: Exception ->
                    Log.e(TAG, "Failed to get last known location: ${e.message}")
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        }
    }

    // Nappi joka avaa PermissionRequestActivityn
    @Composable
    private fun RequestPermissionButton(context: Context) {
        Button(
            text = "Grant Location Permission",
            onClick = {
                val intent = Intent(context, PermissionRequestActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        )
    }

    // Näkymä johon tulee näkyviin seuraavan 3 tunnin lämpötilat nykyisessä sijainnissa
    @Composable
    fun DisplayTemperature(
        context: Context,
        location: Location,
        temperatures: MutableState<WeatherItem>,
        coroutineScope: CoroutineScope
    ) {
        // Haetaan lämpötilatiedot käyttäen coroutinea
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                setUpRequestQueue(context,
                    location.latitude.toString(), location.longitude.toString(), temperatures)
            }
        }

        // Näytä otsikkoteksti
        Text(style = TextStyle(color = ColorProvider(MaterialTheme.colorScheme.onPrimaryContainer),
            fontSize = MaterialTheme.typography.titleSmall.fontSize),
            text = "Temperature for the next 3 hours")

        // Näytä lämpötilatiedot kullekin aikavälille
        for(ind in temperatures.value.temperature.indices) {
                Text(style = TextStyle(color = ColorProvider(MaterialTheme.colorScheme.onPrimaryContainer),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                    text = "${temperatures.value.time[ind]}: ${temperatures.value.temperature[ind]} ºC")
            }
    }
}
