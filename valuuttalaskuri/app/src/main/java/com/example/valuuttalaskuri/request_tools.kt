package com.example.valuuttalaskuri

import android.content.Context
import androidx.compose.runtime.MutableState
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
    text: MutableState<String?>,
    entries: MutableState<List<Cube>>
) {

    requestQueue = Volley.newRequestQueue(context)

    // Instantiate the cache
    val cache = DiskBasedCache(context.cacheDir, 1024 * 1024) // 1MB cap

    // Set up the network to use HttpURLConnection as the HTTP client.
    val network = BasicNetwork(HurlStack())

    requestQueue = RequestQueue(cache, network).apply {
        start()
    }

    stringRequest = getStringRequest(text) { xmlContent ->
        try {
            val parsedEntries = XMLParser().parse(xmlContent.byteInputStream())
            entries.value = parsedEntries
        } catch (e: XmlPullParserException) {
            // Handle the error, e.g., show a message to the user
            text.value = "Failed to parse XML."
        }
    }
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
