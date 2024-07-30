package com.example.koiratreffit.karttaUtils

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.koiratreffit.R
import com.example.koiratreffit.chat.ChatActivity
import com.example.koiratreffit.tapahtumat.DbTapahtumat
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

// Luokka info-ikkunaa varten, jossa kuvataan tapahtumaa tai paikkaa
class CustomInfoWindow(mapView: MapView?, private val isOwner: Boolean, private val eventClass: Class<Any>) : MarkerInfoWindow(R.layout.bonuspack_bubble, mapView) {

    private var markerKey: String? = null // Muuttuja, joka tallentaa markerin avaimen
    private val chatButton = view.findViewById<Button>(R.id.bubble_chat) // Keskustelu-painike infon ikkunassa

    // Funktio, joka suoritetaan, kun info-ikkuna avataan
    override fun onOpen(item: Any) {
        val marker = item as Marker
        val view = view
        val title = view.findViewById<TextView>(R.id.bubble_title)
        val description = view.findViewById<TextView>(R.id.bubble_description)
        val delButton = view.findViewById<Button>(R.id.bubble_del)

        if (isOwner) {
            Log.d("CustomInfoWindow", "Delete button is visible")
            delButton.visibility = View.VISIBLE
            delButton.setOnClickListener {
                marker.infoWindow.close()
                mMapView.overlays.remove(marker)

                markerKey?.let { key ->
                    Log.d("CustomInfoWindow", "Marker with key $key has been deleted")
                    DbTapahtumat().dbDeleteTapahtuma(key)
                } ?: Log.e("CustomInfoWindow", "Marker key is not set")
            }
        } else {
            delButton.visibility = View.GONE // Piilotetaan poistopainike, jos käyttäjä ei ole omistaja
        }

        title.text = marker.title
        description.text = marker.snippet
    }

    override fun onClose() {
    }

    // Asetetaan markerin avain
    fun setMarkerKey(key: String) {
        this.markerKey = key
        setChatButton(key)
    }

    // Asetetaan Keskustelu-painikkeelle kuuntelija, joka avaa chat-toiminnon
    private fun setChatButton(key: String) {
        chatButton.setOnClickListener {
            val chatActivityIntent = Intent(mapView.context, ChatActivity::class.java)
            chatActivityIntent.putExtra("classType", eventClass.simpleName)
            chatActivityIntent.putExtra("key", key)
            startActivity(mapView.context, chatActivityIntent, null)
        }
    }
}