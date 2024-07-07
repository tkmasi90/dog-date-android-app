package com.example.gps

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "SharedPreferences"

// Shared preferences joka tallettaa sijainnin
class SharedPreferences(application: Application) {

    private val sharedPreferences = application.getSharedPreferences("locations_prefs", Context.MODE_PRIVATE)
    private var locationHistory: MutableList<Pair<Double,Double>> = mutableListOf()


    // Tallennetaan historia shared preferenceen json objektina
    fun saveLocation(location: Location) : Boolean {
        loadLocations()

        if(!locationHistory.contains(Pair(location.latitude, location.longitude))) {
            val existingHistoryString = sharedPreferences.getString("locations", null)
            val existingHistoryArray = if (existingHistoryString != null) {
                JSONArray(existingHistoryString)
            } else {
                JSONArray()
            }

            val jsonObject = JSONObject()
            jsonObject.put("lat", location.latitude)
            jsonObject.put("lon", location.longitude)

            existingHistoryArray.put(jsonObject)

            sharedPreferences.edit().putString("locations", existingHistoryArray.toString()).apply()
            return true
        }
        return false
    }

    // Ladataan sijaintihistoria
    private fun loadLocations() {
        val locationHistoryString = sharedPreferences.getString("locations", null)

        if (locationHistoryString != null) {
            val jsonArray = JSONArray(locationHistoryString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.optJSONObject(i)
                if (jsonObject != null) {
                    val lat = jsonObject.getDouble("lat")
                    val lon = jsonObject.getDouble("lon")
                    locationHistory.add(Pair(lat, lon))
                }
            }
        }
    }

    fun getLocationHistory(): List<Pair<Double, Double>> {
        loadLocations()
        return locationHistory
    }

    fun emptyHistory() {
        sharedPreferences.edit().remove("locations").apply()
        locationHistory.clear()
        loadLocations()
    }

    // Poistetaan yksittäinen sijainti historiasta
    fun removeLocation(loc : Pair<Double,Double>) {
        loadLocations()
        if (locationHistory.remove(loc)) {
            sharedPreferences.edit().putString("locations", JSONArray(locationHistory).toString())
                .apply()
            Log.d(TAG, "Location removed")
        } else {
            Log.d(TAG, "Location not found")
        }
    }
}
