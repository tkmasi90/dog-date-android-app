package com.example.temperaturewidget.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun setUpRequestQueue(
    context: Context,
    locLat: String,
    locLon: String,
    weatherItem: MutableState<WeatherItem>
) {

    // Alusta requestQueue uudella Volley-instanssilla
    requestQueue = Volley.newRequestQueue(context)

    // Instantoidaan välimuisti
    val cache = DiskBasedCache(context.cacheDir, 1024 * 1024) // 1MB cap

    // Määritetään verkko käyttämään HttpURLConnectionia HTTP-asiakkaana
    val network = BasicNetwork(HurlStack())

    // Luodaan requestQueue-instanssi
    val requestQueue = RequestQueue(cache, network).apply {
        start()
    }

    // Luodaan StringRequest käyttämällä getStringRequest-funktiota, joka hakee XML-sisällön
    val stringRequest = getStringRequest(locLat,locLon) { xmlContent ->
        CoroutineScope(Dispatchers.IO).launch {
            val parsedWeatherItem = parseLocation(xmlContent)
            withContext(Dispatchers.Main) {
                weatherItem.value = parsedWeatherItem
            }
        }
    }
    // Lisätään StringRequest pyyntöjonoon verkkopyynnön aloittamiseksi
    requestQueue.add(stringRequest)
}

private suspend fun parseLocation (xmlContent : String) : WeatherItem {
    return XMLParser().parseXML(xmlContent.byteInputStream())
}

private fun getStringRequest(locLat : String, locLon : String, onResponse: (String) -> Unit): StringRequest {
    val url = "https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=ecmwf::forecast::surface::point::timevaluepair&latlon=$locLat,$locLon"
    Log.d("WeatherURL", "URL: $url")
    return StringRequest(
        Request.Method.GET, url,
        { response ->
            onResponse(response)
        },
        { // Handle error here if needed
    }
    )
}

