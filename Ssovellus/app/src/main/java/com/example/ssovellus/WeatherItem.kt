package com.example.ssovellus

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver

// Luokka joka pitää sisällään yhden paikan 3 viimeisintä mitattua lämpötila-arvoa
data class WeatherItem(val location : String, val time : List<String>, val temperature : List<String>)

// Määritetään miten WeatherItem tallennetaan ja palautetaan esim. kun ruutu kääntyy
val WeatherItemSaver: Saver<WeatherItem, *> = mapSaver(
    save = {
        mapOf(
            "location" to it.location,
            "time" to it.time,
            "temperature" to it.temperature
        )
    },
    restore = {
        WeatherItem(
            location = it["location"] as String,
            time = it["time"] as List<String>,
            temperature = it["temperature"] as List<String>
        )
    }
)