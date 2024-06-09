package com.example.harjoitus12

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

// Funktio joka on vastuussa kuvan ottamisesta ja tallentamisesta
fun takePhoto (
    imageCapture: ImageCapture?,
    takenImageUri: MutableState<Uri?>,
    context: Context) {

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        return
    }

    val contentResolver = context.contentResolver

    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            Log.d("CameraXApp", "photoDir: ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        .build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraXApp", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri
                takenImageUri.value = savedUri
                Toast.makeText(context, "Photo captured successfully", Toast.LENGTH_SHORT).show()
                Log.d("CameraXApp", "Photo capture succeeded: $savedUri")
            }
        }
    )
}

// Funktio joka on vastuussa videon ottamisesta ja tallentamisesta
fun takeVideo(
    context: Context,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    takenVideoUri: MutableState<Uri?>,
    recording: MutableState<Recording?>,
    isRecording: MutableState<Boolean>
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Camera and audio permissions are required", Toast.LENGTH_SHORT).show()
        return
    }

    if (isRecording.value) {
        // Stop recording
        recording.value?.stop()
        recording.value = null
        isRecording.value = false
    } else {
        // Start recording
        val contentResolver = context.contentResolver

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        val currentVideoCapture = videoCapture.value ?: return
        recording.value = currentVideoCapture.output
            .prepareRecording(context, mediaStoreOutputOptions)
            .withAudioEnabled() // Enable audio if needed
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        Log.d(TAG, "Video recording started")
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            takenVideoUri.value = recordEvent.outputResults.outputUri
                            Log.d(TAG, msg)
                        } else {
                            recording.value?.close()
                            recording.value = null
                            Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                        }
                    }
                }
            }
        isRecording.value = true
    }
}

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val TAG = "CameraFunctions"