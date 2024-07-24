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
    fun showPopUp(mMapView: MapView, point: GeoPoint, context : Context) {
        Log.d(TAG, "Showing popup at: ${point.latitude}, ${point.longitude}")

        val layoutInflater =
            mMapView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = NewMarkerPopupBinding.inflate(layoutInflater)

        val popUp = PopupWindow(mMapView.context)
        popUp.contentView = binding.root
        popUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.isFocusable = true

        val x = 100
        val y = 300
        popUp.setBackgroundDrawable(ColorDrawable())
        popUp.showAtLocation(binding.root, Gravity.NO_GRAVITY, x, y)

        binding.buttonCreate.setOnClickListener {
            if(Markers().addNewMarker(mMapView, binding, context, point)) {
                Toast.makeText(
                    context, "Tapahtuma luotu",
                    Toast.LENGTH_SHORT
                ).show()
                popUp.dismiss()
            } else {
                Toast.makeText(
                    context, "Tapahtumalle täytyy antaa nimi ja kuvaus",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}