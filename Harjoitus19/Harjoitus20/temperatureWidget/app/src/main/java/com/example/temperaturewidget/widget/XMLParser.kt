package com.example.temperaturewidget.widget

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

private const val TAG = "XMLParser"

class XMLParser {
    // Muuttaa XML:n Document instanssiksi jolloin siitä tietojen hakeminen helpottuu
    suspend fun parseXML(inputStream: InputStream): WeatherItem {
        return withContext(Dispatchers.IO) {
            var name = ""
            var timeZone = ""
            val times = mutableListOf<String>()
            val values = mutableListOf<String>()

            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val doc: Document = builder.parse(inputStream)
                doc.documentElement.normalize()

                val nodes : NodeList = doc.getElementsByTagName("wml2:MeasurementTimeseries")
                val tempNode : Node = nodes.item(1)


                // Haetaan halutut tiedot
                val nameNode: NodeList = doc.getElementsByTagName("gml:name")
                val timeZoneNode : NodeList = doc.getElementsByTagName("target:timezone")

                // Nimi talletetaan Mapin avaimeksi
                name = nameNode.item(0).textContent
                // Otetaan aikavyöhykkeen tieto talteen jotta saadaan aika esitettyä oikein
                timeZone = timeZoneNode.item(0).textContent

                if(tempNode.nodeType == Node.ELEMENT_NODE) {
                    val tElement = tempNode as Element
                    val timeNodes: NodeList = tElement.getElementsByTagName("wml2:time")
                    val valueNodes: NodeList = tElement.getElementsByTagName("wml2:value")

                    // Ajat ja arvot talletetaan parin alkioiksi
                    for (i in 0 until 3) {
                        val time = formatTime(timeNodes.item(i).textContent, timeZone)
                        Log.d(TAG, "Time: $time")
                        times.add(time)

                        val temp = valueNodes.item(i).textContent
                        Log.d(TAG, "Temp: $temp")
                        values.add(temp)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "parseXML: ${mapOf(name to Pair(times, values))}")

            return@withContext WeatherItem(name, times.toList(), values.toList())
        }
    }

    // Haluttu aikaformaatti
    private fun formatTime(dateTimeString: String, timeZone: String): String {
        val finZone : ZoneId = ZoneId.of(timeZone)
        return try {
            val instant = Instant.parse(dateTimeString)
            val helsinkiTime = instant.atZone(finZone)
            helsinkiTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            e.printStackTrace()
            dateTimeString
        }
    }
}