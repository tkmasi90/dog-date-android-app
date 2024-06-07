package com.example.harjoitus11

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.harjoitus11.ui.theme.Harjoitus11Theme

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = populateSensorList(sensorManager)

        if (sensors.isNotEmpty()) {
            viewModel.startListening(sensorManager, sensors)
        } else {
            Log.e("MainActivity", "No sensors available")
        }

        setContent {
            Harjoitus11Theme {
                SensorApp(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListening(sensorManager)
    }
}

@Composable
fun SensorApp(viewModel: SensorViewModel) {
    val sensorDataMap = viewModel.sensorDataMap.collectAsState()

    Scaffold(topBar = { TopBar() }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(innerPadding),
            content = {
                items(sensorDataMap.value.toList()) { (type, sensorData) ->
                    SensorItem(sensorData)
                }
            })
    }
}


@Preview
@Composable
private fun SensorAppPreview() {
    val viewModel = SensorViewModel().apply {
        provideMockData()
    }
    SensorApp(viewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    TopAppBar(colors = topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
        title = {
            Text("Sensorit")
        })
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBar()
}
