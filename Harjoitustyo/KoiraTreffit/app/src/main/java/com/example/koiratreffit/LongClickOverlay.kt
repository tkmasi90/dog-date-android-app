package com.example.koiratreffit

import android.view.MotionEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

// Luokka, joka mahdollistaa pitkän painalluksen käsittelyn kartalla
class LongClickOverlay(private val mapView: MapView, private val onLongClick: (GeoPoint) -> Unit) : Overlay() {
    override fun onLongPress(e: MotionEvent?, mapView: MapView?): Boolean {
        if (e != null && mapView != null) {
            val point = mapView.projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
            onLongClick(point)
            return true
        }
        return false
    }
}