package com.example.koiratreffit.tapahtumat

import android.util.Log
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import org.osmdroid.util.GeoPoint

data class Tapahtuma(
    var key: String = "",
    var nimi: String,
    var lat: Double,
    var lon: Double,
    var kuvaus:String,
    var omistaja: String) : DatabaseReference.CompletionListener {

    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
        if (error != null) {
            Log.e("Tapahtuma", "Database operation failed", error.toException())
        } else {
            Log.d("Tapahtuma", "Database operation succeeded")
        }
    }
}