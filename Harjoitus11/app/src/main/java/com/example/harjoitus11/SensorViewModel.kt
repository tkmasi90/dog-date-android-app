package com.example.harjoitus11

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Sensorien antamaa dataa yksinkertaistettu niin että kaikki sensorit antavat joko 3 tai 1 arvoa
// riippuen sensorin palauttaman datan määrästä. Osa arvoista
class SensorData(val type: String = "", val dataList: List<Float>)

class SensorViewModel : ViewModel(), SensorEventListener {
    private val _sensorDataMap = MutableStateFlow<Map<Int, SensorData>>(emptyMap())
    val sensorDataMap: StateFlow<Map<Int, SensorData>> get() = _sensorDataMap

    private val registeredSensors = mutableListOf<Sensor>()

    fun startListening(sensorManager: SensorManager, sensors: List<Sensor?>) {
        sensors.forEach { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (sensor != null) {
                registeredSensors.add(sensor)
            } else {
                Log.e("SensorViewModel", "Sensor is null")
            }
        }
    }

    fun stopListening(sensorManager: SensorManager) {
        registeredSensors.forEach { sensor ->
            sensorManager.unregisterListener(this, sensor)
        }
        registeredSensors.clear()
    }

    fun provideMockData() {
        _sensorDataMap.value = mapOf(
            Sensor.TYPE_ACCELEROMETER to SensorData("Accelerometer", listOf(1.0f, 2.0f, 3.0f)),
            Sensor.TYPE_GYROSCOPE to SensorData("Gyroscope", listOf(4.0f, 5.0f, 6.0f))
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorData = getSensorData(event)

        Log.d("SensorDataUpdate", "Sensor data updated: $sensorData")

        sensorData.let {
            _sensorDataMap.value = _sensorDataMap.value.toMutableMap().apply {
                this[event.sensor.type] = sensorData
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Handle changes in sensor accuracy here
    }
}