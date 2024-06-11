package com.example.askelmittari

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCounterViewModel(application: Application): AndroidViewModel(application), SensorEventListener {

    // Mutable state flow joka pitää kirjaa siitä onko askelten mittaus käynnissä
    var running = MutableStateFlow(false)

    // Mutable state flow joka pitää kirjaa tämänhetkisestä askelmäärästä
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    // Askellaskennan aloituspiste
    private var initialStepCount = 0

    // Mutable state flow joka pitää kirjaa sovelluksen käynnissäoloaikana tallennetuista askeleista
    private val _stepHistory = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val stepHistory: StateFlow<List<Pair<String, Int>>> = _stepHistory

    // Shared preferences joka tallettaa askelmäärähistorian paikallisesti
    private val sharedPreferences = application.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    // Ladataan tallennettu askelmäärähistoria
    init {
        loadSavedSteps()
    }

    // Callback anturitiedon muuttumiselle. Tarkistetaan onko askelten mittaus käynnissä.
    override fun onSensorChanged(event: SensorEvent) {
        if(running.value) {
            // Määritetään askelmäärän aloituspiste, jos sitä ei ole vielä tehty
            if(initialStepCount < 1) {
                initialStepCount = event.values[0].toInt()
            }
            Log.d("SensorDataUpdate", "Initial Step Count: $initialStepCount")

            // Askelmäärä saadaan kun vähennetään nykyinen askelmäärä aloituspisteestä
            _stepCount.value = event.values[0].toInt() - initialStepCount

            Log.d("SensorDataUpdate", "Sensor data updated: ${_stepCount.value}")
        }
    }

    // Callback anturin tarkkuuden muutoksille. Ei toteutettu tässä tapauksessa
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy here
    }

    // Aloitetaan askelmäärän mittauksen kuuntelu. Sensori palauttaa askelmäärän siitä asti kun
    // järjestelmä on viimeksi buutattu
    fun startListening(sensorManager: SensorManager, stepDetectorSensor: Sensor?) {
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.e("StepCounterViewModel", "Step Detector Sensor is null")
    }

    // Tallennetaan nykyinen askelmäärä historiaan
    fun saveSteps() {
        val date = dateFormat.format(Date())
        viewModelScope.launch {
            val newHistory = _stepHistory.value + Pair(date, _stepCount.value)
            _stepHistory.value = newHistory
            saveStepHistory(newHistory)
        }
    }

    // Tallennetaan historia shared preferenceen json objektina
    private fun saveStepHistory(stepHistory: List<Pair<String, Int>>) {
        val jsonArray = JSONArray()
        for (entry in stepHistory) {
            val jsonObject = JSONObject()
            jsonObject.put("date", entry.first)
            jsonObject.put("steps", entry.second)
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString("step_history", jsonArray.toString()).apply()
    }

    // Ladataan tallennettu json muotoinen askelmäärähistoria
    private fun loadSavedSteps() {
        val stepHistoryString = sharedPreferences.getString("step_history", null)
        val stepsList = mutableListOf<Pair<String, Int>>()

        if (stepHistoryString != null) {
            val jsonArray = JSONArray(stepHistoryString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val date = jsonObject.getString("date")
                val steps = jsonObject.getInt("steps")
                stepsList.add(Pair(date, steps))
            }
        }
        _stepHistory.value = stepsList
    }

    // Nollataan nykyinen askelmäärä
    fun clearSteps() {
        _stepCount.value = 0
    }

    // Tyhjentää historian
    fun emptyHistory() {
        _stepHistory.value = emptyList()
        sharedPreferences.edit().remove("step_history").apply()


    }
}

private const val TIME_FORMAT = "dd.MM.yyyy"