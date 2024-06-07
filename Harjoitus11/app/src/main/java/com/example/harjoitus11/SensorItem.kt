package com.example.harjoitus11

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgeDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SensorItem(
    sensorData : SensorData
) {
    Card(modifier = Modifier.fillMaxWidth()
                            .padding(4.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = CardDefaults.outlinedShape
    ) {
        when (sensorData) {
            is SensorData.XYZSensorData -> {
                Text(modifier = Modifier.padding(8.dp),
                    text =
                        "Type: ${sensorData.type}\n" +
                        "X: ${sensorData.x}\n" +
                        "Y: ${sensorData.y}\n" +
                        "Z: ${sensorData.z}"
                )
            }
            is SensorData.OneSensorData -> {
                Text(modifier = Modifier.padding(8.dp),
                    text =
                    "Type: ${sensorData.type}\n"+
                    "Value: ${sensorData.value}")
            }
        }
    }
}