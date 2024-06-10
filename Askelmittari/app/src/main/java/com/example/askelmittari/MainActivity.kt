package com.example.askelmittari

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.askelmittari.ui.theme.AskelmittariTheme
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        enableEdgeToEdge()
        setContent {
            AskelmittariTheme {
                StepCounterApp(sensorManager)
            }
        }
    }
}

@Composable
fun StepCounterApp(sensorManager : SensorManager?) {
    val context = LocalContext.current
    val stepCounterSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    val stepDetectorSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    var hasPermissions by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermissions = isGranted
    }

    // Check and request permission
    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(16.dp)) {
        Text("Oikeudet ACTIVITY_RECOGNITION: $hasPermissions")
        Text("Laitteesta löytyy TYPE_STEP_COUNTER: ${stepCounterSensor != null}")
        Text("Laitteesta löytyy TYPE_STEP_DETECTOR: ${stepDetectorSensor != null}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AskelmittariTheme {
        StepCounterApp(null)
    }
}