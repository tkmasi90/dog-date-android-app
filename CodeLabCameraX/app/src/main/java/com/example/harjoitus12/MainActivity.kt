package com.example.harjoitus12

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.harjoitus12.ui.theme.CodeLabCameraXTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.*

class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeLabCameraXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraApp()
                }
            }
        }

        cameraExecutor = newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

// Varsinainen sovellus joka tarkastaa että kameran käytön ja tiedostojen kirjoittamisen
// ja lukemisen luvat ovat kunnossa jonka jälkeen laukaistaan varsinainen kamerasovellus
@Composable
fun CameraApp() {
    val context = LocalContext.current

    // Muuttuva state joka pitää huolta että luvat on kunnossa
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    (Build.VERSION.SDK_INT > Build.VERSION_CODES.P || (
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            ))
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.all { it.value }
    }

    LaunchedEffect(Unit) {
        checkAndRequestPermissions(hasPermissions, permissionLauncher)
    }

    // Jos luvat kunnossa niin käynnistetään kamerasovellus
    if (hasPermissions) {
        StartCamera(context)
    }
    else {
        Toast.makeText(context,
            "Permission request denied",
            Toast.LENGTH_SHORT).show()
    }
}

// Funktio lupien tarkastamiseen
fun checkAndRequestPermissions(hasPermissions: Boolean, permissionLauncher: ActivityResultLauncher<Array<String>>) {
    if (!hasPermissions) {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }
}

// Varsinainen kamerasovellus
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartCamera(context: Context) {
    val imageCapture = remember { mutableStateOf<ImageCapture?>(ImageCapture.Builder().build()) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(VideoCapture.withOutput(Recorder.Builder().build())) }
    val recording = rememberSaveable { mutableStateOf<Recording?>(null) }
    val takenImageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val takenVideoUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val isRecording = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "CameraX") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                IconButton(onClick = { context.startActivity(Intent(context, ThumbnailViewActivity::class.java))}) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Back")
                }
            }
            )
        },
        
        bottomBar = {
            BottomAppBar(
                modifier = Modifier,
                contentPadding = BottomAppBarDefaults.ContentPadding,
                contentColor = BottomAppBarDefaults.bottomAppBarFabColor
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        takenVideoUri.value = null
                        takePhoto(imageCapture.value, takenImageUri, context)
                    }) {
                        Text("Ota kuva")
                    }

                    Button(onClick = {
                        takenImageUri.value = null
                        takeVideo(context, videoCapture, takenVideoUri, recording, isRecording)
                    }) {
                        Text(if (isRecording.value) "Lopeta video" else "Ota video")
                    }
                }
            }
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                val previewHeightWeight = if (takenImageUri.value != null || takenVideoUri.value != null) 0.7f else 1f
                val imageViewHeightWeight = if (takenImageUri.value != null || takenVideoUri.value != null) 0.35f else 0f

                // Kameran näkymä
                CameraPreviewView(
                    cameraExecutor = newSingleThreadExecutor(),
                    imageCapture = imageCapture,
                    videoCapture = videoCapture,
                    recording = recording,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(previewHeightWeight)
                )

                if (takenImageUri.value != null || takenVideoUri.value != null) {
                    Spacer(modifier = Modifier.weight(0.05f))

                    // Näyttää viimeisimmän otetun kuvan
                    XImageView(
                        uri = takenImageUri.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(imageViewHeightWeight)
                    )
                    // Näyttää viimeisimmän otetun videon
                    XVideoView(
                        uri = takenVideoUri.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(imageViewHeightWeight)
                    )
                }
            }
        }
    )
}


@Preview
@Composable
private fun BottomAppBarPreview() {
    BottomAppBar(modifier = Modifier,
        contentPadding = BottomAppBarDefaults.ContentPadding,
        contentColor = BottomAppBarDefaults.bottomAppBarFabColor) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /*Mock take photo*/ }) {
                Text("Ota kuva")
            }
            Button(onClick = { /*Mock capture video*/ }) {
                Text("Ota video")
            }
        }
    }
}

//@Preview
//@Composable
//private fun TakenImageViewPreview() {
//    TakenImageView(
//        null, Modifier
//            .fillMaxWidth()
//            .weight(imageViewHeight)
//    )
//}

private const val TAG = "CameraXApp"
