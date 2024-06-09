package com.example.harjoitus12

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.harjoitus12.ui.theme.CodeLabCameraXTheme

// Koko näytön videon activity
class VideoFullScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("FullScreenActivity", "onCreate")
        super.onCreate(savedInstanceState)

        val uri = intent.getParcelableExtra<Uri>("uri")
        Log.d("FullScreenActivity", "uri: $uri")

        setContent {
            CodeLabCameraXTheme {
                if (uri != null) {
                    XVideoView(uri, modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black))
                }
            }
        }
    }
}
