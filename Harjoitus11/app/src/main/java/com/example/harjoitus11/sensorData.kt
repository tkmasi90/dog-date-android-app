package com.example.harjoitus11

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log

// kolme arvoa palauttavat sensorit
var xyzSensors = listOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY,
    Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_GYROSCOPE,
    Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ROTATION_VECTOR)
// yhden arvon palauttavat sensorit
var oneSensors = listOf(Sensor.TYPE_LIGHT, Sensor.TYPE_AMBIENT_TEMPERATURE,
    Sensor.TYPE_PRESSURE, Sensor.TYPE_PROXIMITY, Sensor.TYPE_RELATIVE_HUMIDITY,
    Sensor.TYPE_STEP_COUNTER)

fun populateSensorList(sensorManager: SensorManager): List<Sensor?> {
    return xyzSensors.map{ sensorManager.getDefaultSensor(it) } +
            oneSensors.map { sensorManager.getDefaultSensor(it) }
}
// Sensoridata palautetaan sen mukaan onko xyz vai yhden arvon sensor
fun getSensorData(event: SensorEvent) : SensorData {

    Log.d("SensorType", "Sensor type: ${getSensorTypeString(event.sensor.type)}")

    for (value in event.values) {
        Log.d("SensorType", "$value")
    }

    return when (event.sensor.type in xyzSensors) {
        true  -> SensorData.XYZSensorData(
            getSensorTypeString(event.sensor.type),
            event.values[0], event.values[1], event.values[2]
        )
        false -> SensorData.OneSensorData(
            getSensorTypeString(event.sensor.type),
            event.values[0]
        )
    }
}

fun getSensorTypeString(sensorType: Int): String {
    return when (sensorType) {
        Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
        Sensor.TYPE_GYROSCOPE -> "Gyroscope"
        Sensor.TYPE_MAGNETIC_FIELD -> "Magnetic Field"
        Sensor.TYPE_PRESSURE -> "Pressure"
        Sensor.TYPE_LIGHT -> "Light"
        Sensor.TYPE_PROXIMITY -> "Proximity"
        Sensor.TYPE_GRAVITY -> "Gravity"
        Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
        Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
        Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ambient Temperature"
        Sensor.TYPE_RELATIVE_HUMIDITY -> "Relative humidity"
        Sensor.TYPE_STEP_COUNTER -> "Step counter"
        else -> "Unknown Sensor"
    }
}
