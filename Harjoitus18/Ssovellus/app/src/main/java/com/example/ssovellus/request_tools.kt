package com.example.ssovellus

import android.content.Context
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
    input: String,
    weatherItem: MutableState<WeatherItem>,
    onEntriesUpdated: () -> Unit
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
    val stringRequest = getStringRequest(input) { xmlContent ->
        CoroutineScope(Dispatchers.IO).launch {
            val parsedWeatherItem = parseLocation(xmlContent)
            withContext(Dispatchers.Main) {
                weatherItem.value = parsedWeatherItem
                onEntriesUpdated()
            }
        }
    }
    // Lisätään StringRequest pyyntöjonoon verkkopyynnön aloittamiseksi
    requestQueue.add(stringRequest)
}

private suspend fun parseLocation (xmlContent : String) : WeatherItem {
    return XMLParser().parseXML(xmlContent.byteInputStream())
}

private fun getStringRequest(location : String, onResponse: (String) -> Unit): StringRequest {
    val url = "https://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=$location&parameters=t2m&"
    return StringRequest(
        Request.Method.GET, url,
        { response ->
            onResponse(response)
        },
        { // Handle error here if needed
    }
    )
}

