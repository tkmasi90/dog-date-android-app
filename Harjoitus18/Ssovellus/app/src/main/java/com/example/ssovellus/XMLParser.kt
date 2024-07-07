package com.example.ssovellus

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

private const val TAG = "XMLParser"

class XMLParser {

    // Muuttaa XML:n Document instanssiksi jolloin siitä tietojen hakeminen helpottuu
    suspend fun parseXML(inputStream: InputStream): WeatherItem {
        return withContext(Dispatchers.IO) {
            var name = ""
            val times = mutableListOf<String>()
            val values = mutableListOf<String>()

            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val doc: Document = builder.parse(inputStream)
                doc.documentElement.normalize()

                // Haetaan halutut tiedot
                val nameNode: NodeList = doc.getElementsByTagName("gml:name")
                val timeNodes: NodeList = doc.getElementsByTagName("wml2:time")
                val valueNodes: NodeList = doc.getElementsByTagName("wml2:value")

                // Nimi talletetaan Mapin avaimeksi
                if (nameNode.length > 0) {
                    name = nameNode.item(0).textContent
                }

                // Ajat ja arvot talletetaan parin alkioiksi
                for (i in 0 until 5) {
                    val time = formatTime(timeNodes.item(i).textContent)
                    times.add(time)
                }

                for (i in 0 until 5) {
                    val value = valueNodes.item(i).textContent
                    values.add(value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "parseXML: ${mapOf(name to Pair(times, values))}")

            return@withContext WeatherItem(name, times.toList(), values.toList())
        }
    }

    // Haluttu aikaformaatti
    private fun formatTime(dateTimeString: String): String {
        return try {
            val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        } catch (e: Exception) {
            e.printStackTrace()
            dateTimeString
        }
    }
}
