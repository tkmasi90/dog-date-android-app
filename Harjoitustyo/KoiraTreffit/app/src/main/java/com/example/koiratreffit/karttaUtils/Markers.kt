package com.example.koiratreffit.karttaUtils

import android.content.Context
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.koiratreffit.koirapuistot.DbKoirapuistot
import com.example.koiratreffit.koirapuistot.Koirapuisto
import com.example.koiratreffit.R
import com.example.koiratreffit.databinding.NewMarkerPopupBinding
import com.example.koiratreffit.tapahtumat.DbTapahtumat
import com.example.koiratreffit.tapahtumat.Tapahtuma
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Markers {
    private val user = FirebaseAuth.getInstance().currentUser

    suspend fun getMarkersFromDb(mMapView : MapView, context : Context) {
        val kpList: List<Koirapuisto> = DbKoirapuistot().getKoirapuistot()
        val tapList: List<Tapahtuma> = DbTapahtumat().getTapahtumat()

        kpList.forEach { kp ->
            val marker = createMarker(mMapView, context, kp, R.drawable.baseline_pets_24, false, kp.key)
            mMapView.overlays.add(marker)
        }

        tapList.forEach { tap ->
            val isOwner = tap.omistaja == user?.uid
            val marker = createMarker(mMapView, context, tap, R.drawable.baseline_emoji_events_24, isOwner, tap.key)
            mMapView.overlays.add(marker)
        }
    }

    private fun createMarker(mMapView: MapView,
                             context: Context,
                             event: Any,
                             drawableId: Int,
                             isOwner: Boolean,
                             key: String)
    : Marker {

        val location = when (event) {
            is Koirapuisto -> GeoPoint(event.lat, event.lon)
            is Tapahtuma -> GeoPoint(event.lat, event.lon)
            else -> throw IllegalArgumentException("Unknown event type")
        }

        val title = when (event) {
            is Koirapuisto -> event.nimi
            is Tapahtuma -> event.nimi
            else -> throw IllegalArgumentException("Unknown event type")
        }

        val description = when (event) {
            is Koirapuisto -> event.kuvaus
            is Tapahtuma -> event.kuvaus
            else -> throw IllegalArgumentException("Unknown event type")
        }


        return Marker(mMapView).apply {
            icon = ContextCompat.getDrawable(context, drawableId)
            position = location
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_BOTTOM)
            isDraggable = false
            this.title = title
            this.snippet = description
            val infoWindow = CustomInfoWindow(mMapView, isOwner, event.javaClass)
            infoWindow.setMarkerKey(key)
            this.infoWindow = infoWindow
            setInfoWindowAnchor(0.5F, -1F)
        }
    }

    fun addNewMarker(mMapView: MapView, binding: NewMarkerPopupBinding, context: Context, point: GeoPoint): Boolean {
        val title = binding.root.findViewById<EditText>(R.id.textTitleInput).text.toString()
        val description = binding.root.findViewById<EditText>(R.id.textDescInput).text.toString()

        if (title.isEmpty() || description.isEmpty()) return false

        val tapahtuma = Tapahtuma(nimi = title, lat = point.latitude, lon = point.longitude, kuvaus = description, omistaja = user?.uid.toString())
        val key = DbTapahtumat().dbAddTapahtuma(tapahtuma) ?: return false

        val marker = createMarker(mMapView, context, tapahtuma, R.drawable.baseline_emoji_events_24, true, key)
        mMapView.overlays.add(marker)
        mMapView.invalidate()
        return true
    }
}