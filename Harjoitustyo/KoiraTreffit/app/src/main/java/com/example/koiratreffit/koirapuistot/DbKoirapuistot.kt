package com.example.koiratreffit.koirapuistot

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

private const val TAG = "dbKoirapuisto"

class DbKoirapuistot {
    private var dbKp: DatabaseReference = FirebaseDatabase.getInstance().getReference("koirapuistot")
    private var kpList: List<Koirapuisto> = emptyList()

    private suspend fun fetchKoirapuistot() {
        try {
            val snapshot = dbKp.get().await()
            kpList = snapshot.children.mapNotNull {
                val name = it.child("nimi").getValue(String::class.java)
                val lat = it.child("lat").getValue(Double::class.java)
                val lon = it.child("lon").getValue(Double::class.java)
                val city = it.child("kaupunki").getValue(String::class.java) ?: "Kaupunki puuttuu"
                val description = it.child("kuvaus").getValue(String::class.java) ?: "Kuvaus puutttuu"
                val key = it.key

                if (name != null && lat != null && lon != null && key != null) {
                    Log.d(TAG, "Adding koirapuisto: $name")
                    Koirapuisto(key, name, city, lat, lon, description)
                } else {
                    Log.d(TAG, "Skipping koirapuisto: $name")
                    null  // Skip this entry if any required field is null
                }
            }
            Log.d(TAG, "Koirapuisto list loaded: $kpList")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading koirapuisto list", e)
        }
    }

    suspend fun getKoirapuistot(): List<Koirapuisto> {

        fetchKoirapuistot()

        return kpList
    }

}