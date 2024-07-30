package com.example.koiratreffit.karttaUtils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.example.koiratreffit.databinding.NewMarkerPopupBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private const val TAG = "PopUp"

class PopUpWindow {

    // Funktio, joka näyttää popup-ikkunan kartalla annetussa sijainnissa
    fun showPopUp(mMapView: MapView, point: GeoPoint, context : Context) {
        Log.d(TAG, "Showing popup at: ${point.latitude}, ${point.longitude}")

        // Haetaan LayoutInflater, jota käytetään popup-ikkunan näkymän luomiseen
        val layoutInflater =
            mMapView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Luodaan binding-objekti popup-ikkunan näkymälle
        val binding = NewMarkerPopupBinding.inflate(layoutInflater)

        // Luodaan ja alustetaan PopupWindow-objekti
        val popUp = PopupWindow(mMapView.context)
        popUp.contentView = binding.root
        popUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.isFocusable = true

        // Määritellään popupin sijainti kartalla
        val x = 100
        val y = 300
        popUp.setBackgroundDrawable(ColorDrawable())
        popUp.showAtLocation(binding.root, Gravity.NO_GRAVITY, x, y)

        // Asetetaan kuuntelija "Luo" -painikkeelle
        binding.buttonCreate.setOnClickListener {
            if(Markers().addNewMarker(mMapView, binding, context, point)) {
                Toast.makeText(
                    context, "Tapahtuma luotu",
                    Toast.LENGTH_SHORT
                ).show()
                popUp.dismiss() // Suljetaan popup-ikkuna
            } else {
                Toast.makeText(
                    context, "Tapahtumalle täytyy antaa nimi ja kuvaus",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}