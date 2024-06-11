package com.example.askelmittari

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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.example.askelmittari.ui.theme.AskelmittariTheme
import android.Manifest
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private val viewModel: StepCounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val stepCounterSensor : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetectorSensor : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        viewModel.startListening(sensorManager, stepCounterSensor)

        enableEdgeToEdge()
        setContent {
            AskelmittariTheme {
                StepCounterApp(viewModel, stepCounterSensor, stepDetectorSensor)
            }
        }
    }
}

@Composable
fun StepCounterApp(viewModel: StepCounterViewModel, stepCounterSensor: Sensor?, stepDetectorSensor: Sensor?) {
    val context = LocalContext.current

    // Käyttöoikeus state
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher, joka pyytää käyttäjältä käyttöoikeuksia
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermissions = isGranted
        logPermissions(isGranted, stepCounterSensor, stepDetectorSensor)
    }

    // Tarkista, onko luvat myönnetty, muussa tapauksessa käynnistä lupapyyntö
    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }
    // Jos luvat on ok, avataan askelmittausnäkymä, muuten annetaan virheilmoitus
    if (hasPermissions) {
        StepCounter(viewModel)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.permission_not_granted), textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCounter(viewModel: StepCounterViewModel) {
    val context = LocalContext.current
    // Näkymä on rakennettu käyttäen Scaffoldia, johon on määritetty yläpalkki, alapalkki ja
    // keskelle muuttuvat komponentit.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.askelmittari),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold)},

                navigationIcon = {
                    IconButton(onClick = { context.startActivity(Intent(context,
                        SavedStepsViewActivity::class.java)) }) {
                        Icon(
                            painterResource(R.drawable.baseline_history_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                contentPadding = BottomAppBarDefaults.ContentPadding)
                {
                    Spacer(modifier = Modifier.weight(0.5f))
                    // Play-nappi. Muuttaa running arvon trueksi ja asettaa askelmäärän nollaksi
                    FilledIconButton(onClick = {
                        viewModel.running.value = true
                        viewModel.clearSteps() }) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // Nollaus-nappi. Tyhjentää askelmäärän
                    FilledIconButton(onClick = {
                        viewModel.running.value = false
                        viewModel.clearSteps()
                    }) {
                        Icon(
                            painterResource(R.drawable.baseline_refresh_24),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // Save-nappi. Tallentaa nykyisen askelmittauksen
                    FilledIconButton(onClick = { viewModel.saveSteps()
                        viewModel.running.value = false
                        viewModel.clearSteps()
                        Toast.makeText(context,
                            context.getString(R.string.steps_saved), Toast.LENGTH_SHORT).show()}
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_save_alt_24),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
        },
    ) { innerPadding ->
        Column(modifier = Modifier

            .fillMaxSize()
            .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            // Haetaan varsinainen sisältö
            GetContent(viewModel)

        }
    }
}

// Logataan harjoituksessa 13 tehdyt tarkastelut
fun logPermissions(hasPermissions: Boolean, stepCounterSensor: Sensor?, stepDetectorSensor: Sensor?) {
    Log.d("Permissions", "ACTIVITY_RECOGNITION: $hasPermissions")
    Log.d("Permissions", "TYPE_STEP_COUNTER: ${stepCounterSensor != null}")
    Log.d("Permissions", "TYPE_STEP_DETECTOR: ${stepDetectorSensor != null}")
}

// Tuo sisällön näkymään
@Composable
fun GetContent(viewModel: StepCounterViewModel) {
    AskelmittariTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Haetaan nykyinen askelmäärä
            val steps by viewModel.stepCount.collectAsState()

            // Ilmoitetaan jos sovellus on tauolla
            if(!viewModel.running.collectAsState().value) {
                Text(text = stringResource(R.string.paused),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp))
            }
            // Tuodaan näkyviin askelmäärä
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.steps),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = steps.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 32.sp)
            }
        }
    }
}

// Näkymän esikatselutila
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AskelmittariPreview() {
    AskelmittariTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.askelmittari),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold)},

                    navigationIcon = {
                        // Historia-nappi, näyttää tallennetut askelmittaukset
                        FilledIconButton(onClick = {  }) {
                            Icon(
                                painterResource(R.drawable.baseline_history_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    contentPadding = BottomAppBarDefaults.ContentPadding)
                {
                    Spacer(modifier = Modifier.weight(0.5f))
                    // Play-nappi. Muuttaa running arvon trueksi ja käynnistää näin askelten mittauksen
                    FilledIconButton(onClick = {  }) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // Nollausnappi, joka asettaa askelmään nollaksi
                    FilledIconButton(onClick = {  }) {
                        Icon(
                            painterResource(R.drawable.baseline_refresh_24),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // Save-nappi. Tallentaa nykyisen askelmittauksen
                    FilledIconButton(onClick = { }
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_save_alt_24),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            },
        ) { innerPadding ->
            Column(modifier = Modifier

                .fillMaxSize()
                .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text("Askeleita: 0", textAlign = TextAlign.Center)

            }
        }

    }
}

