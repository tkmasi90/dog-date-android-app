package com.example.valuuttalaskuri

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.example.valuuttalaskuri.ui.theme.ValuuttalaskuriTheme


private const val TAG = "Valuuttalaskuri"
lateinit var stringRequest: StringRequest
lateinit var requestQueue: RequestQueue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValuuttalaskuriTheme {
                Scaffold {
                    RunApp(it)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(::requestQueue.isInitialized)
            requestQueue.cancelAll(TAG)
    }
}

@Composable
fun RunApp(paddingValues : PaddingValues) {

    val context = LocalContext.current
    val rates : MutableState<String?> = remember { mutableStateOf(null) }
    val entries = remember { mutableStateOf<List<Cube>>(emptyList()) }
    val amount = remember { mutableFloatStateOf(Float.NaN) }

    setUpRequestQueue(context, rates, entries)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        DisplayInput(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            amount = amount)

        DisplayResults(
            modifier = Modifier
            .fillMaxSize()
            .weight(1f),
            entries = entries.value,
            amount = amount)
    }
}

@Composable
fun DisplayInput(modifier: Modifier = Modifier, amount: MutableFloatState) {

    val inputValue = remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = inputValue.value,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number),
        onValueChange = {
            // Update the input value state
            inputValue.value = it
            // Try to parse the float from the input string
            amount.floatValue = try {
                it.toFloat()
            } catch (e: NumberFormatException) {
                Float.NaN // Handle invalid input gracefully
            }
        },
        label = { Text("Syötä summa (€)") }
    )
}

@Composable
fun DisplayResults(modifier: Modifier, entries: List<Cube>, amount: MutableFloatState) {
    LazyColumn(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            items(entries) { entry ->
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    val calculatedValue = if (amount.floatValue.isNaN() || amount.floatValue.isInfinite()) {
                        0.0f.toString()
                    } else {
                        (entry.rate * amount.floatValue).toString()
                    }

                    Text(text = calculatedValue)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = entry.currency)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        })
}

@ThemePreviews
@OrientationPreviews
@Composable
fun GreetingPreview() {
    val mockAmount : MutableFloatState = remember { mutableFloatStateOf(123.45f) }
    val mockEntries: List<Cube> = listOf(
        Cube("EUR", 1.0f), Cube(
            "USD",
            1.2345f
        ), Cube("JPY", 123.45f)
    )

    ValuuttalaskuriTheme {
        Scaffold {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {

                    DisplayInput(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        amount = mockAmount)

                    DisplayResults(
                        modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                        entries = mockEntries,
                        amount = mockAmount)
            }
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
annotation class ThemePreviews

@Preview(name = "Landscape Mode", showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 640)
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PIXEL_4)
annotation class OrientationPreviews