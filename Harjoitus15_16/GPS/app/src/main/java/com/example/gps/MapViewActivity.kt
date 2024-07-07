package com.example.gps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


private const val TAG = "MapActivity"
private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 98


class MapViewActivity : AppCompatActivity() {

    private lateinit var mMapView: MapView
    private lateinit var locationList: List<Pair<Double, Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        val ctx = this.applicationContext
        loadConfiguration(this)

        setupMapView()
        setupLocationOverlay(ctx)
        loadLocationList()
        addMarkers()

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun setupMapView() {
        mMapView = findViewById(R.id.mapview)
        mMapView.setTileSource(TileSourceFactory.MAPNIK)
        mMapView.zoomController.setZoomInEnabled(true)
        mMapView.setMultiTouchControls(true)
    }

    private fun setupLocationOverlay(ctx: Context) {
        val mapController = mMapView.controller
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), mMapView)
        locationOverlay.disableMyLocation()
        locationOverlay.disableFollowLocation()
        locationOverlay.isDrawAccuracyEnabled = true

        // Kerrotaan käyttäjälle että sijaintia haetaan
        val snackbar = Snackbar.make(mMapView, "Haetaan sijaintia", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        // Haetaan puhelimen nykyinen sijainti ja keskitetään kartta sen mukaan jahka se on löydetty
        locationOverlay.runOnFirstFix {
            val myLocation = locationOverlay.myLocation
            if (myLocation != null) {
                Log.d(TAG, "MyLocation: ${myLocation.latitude}, ${myLocation.longitude}")
                runOnUiThread {
                    mapController.setCenter(myLocation)
                    mapController.animateTo(locationOverlay.myLocation)
                    mapController.setZoom(15.0)
                    snackbar.dismiss() // Piilotetaan snackbar kun sijainti on löytynyt
                }
            } else Log.d(TAG, "MyLocation is null")
        }

        mMapView.overlays.add(locationOverlay)
    }

    // Haetaan sijaintilista SharedPreferencesista
    private fun loadLocationList() {
        locationList = SharedPreferences(application).getLocationHistory()
    }

    // Lisätään markerit kartalle sijaintilistan perusteella
    private fun addMarkers() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Poista sijainti")
            .setTitle("Haluatko varmasti poistaa tämän sijainnin")

        for (location in locationList) {
            val gPt = GeoPoint(location.first, location.second)
            val marker = Marker(mMapView).apply {
                title = "Sijainti"
                snippet = "Leveys: ${location.first} , Pituus: ${location.second}"
                icon = ContextCompat.getDrawable(this@MapViewActivity, R.drawable.baseline_location_pin_24)
                position = gPt
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                isDraggable = true
            }

            setupMarkerDragListener(marker, builder, location)
            mMapView.overlays.add(marker)
        }
    }

    // Joutuu hiukan kikkailemaan markerin poiston kanssa, koska markerilla ei ole
    // setOnMarkerHoldListener-metodia mutta tämä ajaa suurinpiirtein saman asian
    // Eli painamalla markeria pitkään saa poistettua sen ja sen sijainnin SharedPreferencesstä
    private fun setupMarkerDragListener(marker: Marker, builder: AlertDialog.Builder, location: Pair<Double, Double>) {
        marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker?) {
                Log.d(TAG, "Marker dragged")
                val dialog: AlertDialog = builder
                    .setPositiveButton("Kyllä") { _, _ ->
                        // Poistetaan marker ja sijainti SharedPreferencesstä
                        SharedPreferences(application).removeLocation(location)
                        mMapView.overlays.remove(marker)
                        mMapView.invalidate()
                        Log.d(TAG, "Location removed")
                    }
                    .setNegativeButton("Ei") { dialogInterface, _ ->
                        // Palautetaan marker alkuperäiseen paikkaan jos valitaan Ei
                        if (marker != null) {
                            marker.position = GeoPoint(location.first, location.second)
                        }
                        dialogInterface.dismiss() // Suljetaan dialogi
                    }
                    .create()
                dialog.show()
            }
            override fun onMarkerDrag(marker: Marker?) {}
            override fun onMarkerDragEnd(marker: Marker?) {}
        })
    }

    private fun loadConfiguration(ctx: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        Configuration.getInstance().load(ctx, sharedPreferences)
    }

}

