package com.example.harjoitus11

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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

/* Palauttaa tiedot sensorista. Sensorin eri value arvot esitetään yksinkertaisuuden vuoksi
*  vain numeroa käyttäen, koska erilaisia sensoridatoja on niin paljon  */
@Composable
fun SensorItem(
    sensorData : SensorData
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(8.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = CardDefaults.outlinedShape
    ) {
        Text(modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            text =
            "Type: ${sensorData.type}\n")
        for((num, data) in sensorData.dataList.withIndex()) {
            if(data != 0.toFloat()) {
                Text(modifier = Modifier.padding(start = 16.dp),
                    text =
                    "Data $num: $data")
            }
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
    }
}