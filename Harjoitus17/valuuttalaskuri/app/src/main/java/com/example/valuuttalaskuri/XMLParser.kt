package com.example.valuuttalaskuri

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null
private const val TAG = "XMLParser"

// Valuutta class, nimetty Cubeksi XML:n Tagien mukaan
data class Cube(val currency: String, val rate: Float)

class XMLParser {
    // Jäsentää XML-tietoja sisältävän InputStreamin ja palauttaa lopullisen luettelon Cube-objekteista.
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<Cube> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    // Lukee XML-syötteen ja palauttaa luettelon Cube-objekteista.
    // Etsii "Cube"-tageja valuuttatietojen lukemiseksi.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Cube> {
        val entries = mutableListOf<Cube>()

        parser.require(XmlPullParser.START_TAG, ns, "Envelope")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the Cube tag.
            if (parser.name == "Cube") {
                readTimeCube(parser, entries)
            } else {
                skip(parser)
            }
        }
        return entries
    }

    // Lukee "Cube"-tagin joka liittyy aika-dataan ja siirtyy hierarkiassa eteenpäin valuuttadataan
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTimeCube(parser: XmlPullParser, entries: MutableList<Cube>) {
        parser.require(XmlPullParser.START_TAG, ns, "Cube")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "Cube") {
                readCurrencyCube(parser, entries)
            } else {
                skip(parser)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "Cube")
    }

    // Lukee Cube-elementtejä, jotka sisältävät valuutta- ja kurssimääritteitä.
    // Lisää jäsennetyt valuuttatiedot listaan.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCurrencyCube(parser: XmlPullParser, entries: MutableList<Cube>) {
        parser.require(XmlPullParser.START_TAG, ns, "Cube")
        val currency = parser.getAttributeValue(null, "currency")
        val rate = parser.getAttributeValue(null, "rate")

        if (currency != null && rate != null) {
            Log.d(TAG, "readCurrencyCube: currency $currency, rate $rate")
            entries.add(Cube(currency, rate.toFloat()))
        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "Cube") {
                // Found a nested Cube, process it
                readCurrencyCube(parser, entries)
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "Cube")
    }

    // Skippaa ei-kiinnostavat tagit
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
