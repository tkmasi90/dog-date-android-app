package com.example.koiratreffit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.koiratreffit.karttaUtils.Markers
import com.example.koiratreffit.karttaUtils.PopUpWindow
import com.example.koiratreffit.tapahtumat.DbTapahtumat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


private const val TAG = "MapActivity"
private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 98


class MapViewActivity : AppCompatActivity() {

    private lateinit var mMapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        val mainView = findViewById<View>(R.id.mapview)

        // Asetetaan ikkunan täytteet pääkarttanäkymälle
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asetetaan työkalupalkki
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Pyydä tarvittavia lupia sijainnin ja tallennuksen käyttöön
        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        val ctx = this.applicationContext
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        setupMapView()
        setupLocationOverlay(ctx)
        setCustomMarker()

        // Käynnistä coroutine, joka lisää merkkejä kartalle
        CoroutineScope(Dispatchers.Main).launch {
            val locationsDeferred = async { addMarkers() }
            locationsDeferred.await()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> { // Tarkista, onko uloskirjautumiskomento valittu
                logout() // Suorita uloskirjautuminen
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    // Pyydä käyttäjältä lupia, jos niitä ei ole myönnetty
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

    // Aseta karttanäkymä ja sen ominaisuudet
    private fun setupMapView() {
        mMapView = findViewById(R.id.mapview)
        mMapView.setTileSource(TileSourceFactory.MAPNIK)
        mMapView.zoomController.setZoomInEnabled(true)
        mMapView.setMultiTouchControls(true)
    }

    // Aseta overlay karttaan
    private fun setupLocationOverlay(ctx: Context) {
        val mapController = mMapView.controller
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), mMapView)
        locationOverlay.disableMyLocation()
        locationOverlay.disableFollowLocation()
        locationOverlay.isDrawAccuracyEnabled = true

        // Kerrotaan käyttäjälle että sijaintia haetaan
        val snackbar = Snackbar.make(mMapView, "Haetaan sijaintia", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        // Hae puhelimen nykyinen sijainti ja keskitä kartta sen mukaan
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

    // Lisätään markerit kartalle sijaintilistan perusteella
    private suspend fun addMarkers() {
        Markers().getMarkersFromDb(mMapView, this)
    }

    // Aseta mukautettu merkki pitkän painalluksen yhteydessä
    private fun setCustomMarker() {
        val longClickOverlay = LongClickOverlay(mMapView) { point ->
            Log.d(TAG, "Map long-clicked at: ${point.latitude}, ${point.longitude}")
            PopUpWindow().showPopUp(mMapView, point, this)
        }
        mMapView.overlays.add(longClickOverlay) // Lisää pitkän painalluksen overlay karttaan
    }

    // Uloskirjautumistoiminto
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = android.content.Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

