package com.example.koiratreffit.tapahtumat

import android.util.Log
import android.view.View
import android.widget.Button
import com.example.koiratreffit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint

private const val TAG = "DbTapahtumat"

class DbTapahtumat {
    // Hakee nykyisen käyttäjän tiedot
    private val user = FirebaseAuth.getInstance().currentUser
    // Viittaus "tapahtumat"-tietokannan juureen
    private var dbTap: DatabaseReference = FirebaseDatabase.getInstance().getReference("tapahtumat")
    private var tapList: MutableList<Tapahtuma> = mutableListOf()

    // Suspend-toiminto, joka hakee tapahtumat tietokannasta
    private suspend fun fetchTapahtumat() {
        try {
            val snapshot = dbTap.get().await()
            for(tapahtuma in snapshot.children.filterNotNull()) {
                val key = tapahtuma.child("key").getValue(String::class.java)
                val name = tapahtuma.child("nimi").getValue(String::class.java)
                val lat = tapahtuma.child("lat").getValue(Double::class.java)
                val lon = tapahtuma.child("lon").getValue(Double::class.java)
                val description = tapahtuma.child("kuvaus").getValue(String::class.java)?.replace("_n","\n")
                    ?: "Kuvaus puutttuu"
                val owner = user?.uid

                // Tarkistetaan, että kaikki tarvittavat kentät ovat olemassa
                if (key != null && name != null && lat != null && lon != null && owner != null) {
                    tapList.add(Tapahtuma(key, name, lat, lon, description, owner))

                }
            }
            Log.d(TAG, "Tapahtuma list loaded: $tapList")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading tapahtuma list", e)
        }
    }

    // Julkinen suspend-toiminto, joka palauttaa tapahtumat
    suspend fun getTapahtumat(): List<Tapahtuma> {
        fetchTapahtumat()
        return tapList
    }

    // Funktio, joka lisää uuden tapahtuman tietokantaan
    fun dbAddTapahtuma(tapahtuma: Tapahtuma) : String? {
        val key = dbTap.push().key
        if (key != null) {
            tapahtuma.key = key
            dbTap.child(key).setValue(tapahtuma)
        }
        return key
    }

    // Funktio, joka poistaa tapahtuman tietokannasta
    fun dbDeleteTapahtuma(key : String) {
        dbTap.child(key).removeValue()
    }
}