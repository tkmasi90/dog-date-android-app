package com.example.valuuttalaskuri

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.xmlpull.v1.XmlPullParserException

fun setUpRequestQueue(
    context: Context,
    entries: MutableState<List<Cube>>
) {

    val text = mutableStateOf<String?>(null)
    // Alusta requestQueue uudella Volley-instanssilla
    requestQueue = Volley.newRequestQueue(context)
    // Instantoidaan välimuisti
    val cache = DiskBasedCache(context.cacheDir, 1024 * 1024) // 1MB cap

    // Määritetään verkko käyttämään HttpURLConnectionia HTTP-asiakkaana
    val network = BasicNetwork(HurlStack())

    // Luodaan requestQueue-instanssi
    requestQueue = RequestQueue(cache, network).apply {
        start()
    }

    // Luo StringRequest käyttämällä getStringRequest-funktiota, joka hakee XML-sisällön
    stringRequest = getStringRequest(text) { xmlContent ->
        try {
            val parsedEntries = XMLParser().parse(xmlContent.byteInputStream())
            entries.value = parsedEntries
        } catch (e: XmlPullParserException) {
            text.value = "Failed to parse XML."
        }
    }
    // Lisätään StringRequest pyyntöjonoon verkkopyynnön aloittamiseksi
    requestQueue.add(stringRequest)
}

private fun getStringRequest(text : MutableState<String?>, onResponse: (String) -> Unit): StringRequest {
    val url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
    return StringRequest(
        Request.Method.GET, url,
        { response ->
            onResponse(response)
        },
        { text.value = "That didn't work!" }
    )
}

