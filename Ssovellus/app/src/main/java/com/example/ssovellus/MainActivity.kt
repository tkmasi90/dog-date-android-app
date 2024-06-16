package com.example.ssovellus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.RequestQueue
import com.example.ssovellus.ui.theme.SääsovellusTheme
import kotlinx.coroutines.launch

lateinit var requestQueue: RequestQueue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SääsovellusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherApp(modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherApp(modifier: Modifier = Modifier,
               initialEntries: Map<String, Pair<MutableList<String>, MutableList<String>>> = emptyMap()) {
    val context = LocalContext.current
    val inputValue = rememberSaveable { mutableStateOf("") }
    val entries = rememberSaveable { mutableStateOf(initialEntries) }
    val displayWeather = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(Modifier.fillMaxSize()
        .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(Modifier.fillMaxWidth()
            .padding(bottom = 20.dp)
            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(2f),
                value = inputValue.value,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text),
                onValueChange = {
                    // Päivitetään inputValue Staten arvo
                    inputValue.value = it
                },
                label = { Text("Hae paikkakuntaa") }
            )
            // Käytetään courutineScopea jotta sää tulee näkyviin vasta kun sen haku on valmis
            FilledTonalButton(modifier = Modifier.padding(start = 8.dp)
                .weight(0.7f),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                onClick = {
                    focusManager.clearFocus() // Piilotetaan näppäimistö kun nappulaa painetaan
                    coroutineScope.launch {
                        setUpRequestQueue(context, inputValue.value.trim(), entries) {
                            displayWeather.value = true
                            inputValue.value = ""
                        }
                    }
            }) {
                Text("Hae")
            }
        }

        // Näytetään sää vasta kun nappia on painettu
        if (displayWeather.value) {
            if(!displayWeather(entries = entries, modifier = modifier))
            {
                Toast.makeText(context, "Ei tuloksia", Toast.LENGTH_SHORT).show()
                displayWeather.value = false
            }
        }
    }
}
// Näyttää paikan ja kolme viimeisintä mittaustulosta. Palauttaa false jos paikkaa ei löydy
@Composable
fun displayWeather(entries : MutableState<Map<String, Pair<MutableList<String>, MutableList<String>>>>, modifier: Modifier) : Boolean {
    if (entries.value.isNotEmpty()) {
        val firstEntry = entries.value.entries.first()
        if (firstEntry.value.first.isNotEmpty() && firstEntry.value.second.isNotEmpty()) {
            Text(
                text = "Paikka: ${firstEntry.key}",
                modifier = Modifier.padding(bottom = 16.dp),
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp
            )

            for (entry in entries.value.entries) {
                val times = entry.value.first
                val temperatures = entry.value.second

                for (i in times.indices) {
                    Row(verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Aika: ${times.elementAtOrNull(i) ?: "N/A"}",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = "Lämpötila: ${temperatures.elementAtOrNull(i) ?: "N/A"}",
                        )
                    }
                }
            }
        } else {
            return false
        }
    } else {
        return false
    }
    return true
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val mockData = remember {
        mapOf("Raahe" to Pair(mutableListOf(
            "19:40:00", "19:50:00", "20:00:00"
        ), mutableListOf("11.8", "11.8", "11.8"))) }
    SääsovellusTheme {
        WeatherApp(modifier = Modifier, mockData)
    }
}