package com.example.harjoitus12

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File

// Lataa kaikki sovelluksella otettujen kuvien ja videoiden thumbnailit ja lisää ne LazyColumneihin
// Thumbnailia klikkaamalla aukeaa kuva/video koko näytölle
@Composable
fun ThumbnailListContent() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val thumbnailHeight = screenWidth / 1.5f
    val imageFiles by remember { mutableStateOf(getImageFiles(context)) }
    val videoFiles by remember { mutableStateOf(getVideoFiles(context)) }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(text = "Images",
                textAlign = TextAlign.Center,
                fontSize = 24.sp)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (imageFiles != null) {
                    items(imageFiles!!.size) { file ->
                        // Display each image using XImageView
                        XImageViewWithOnClick(
                            uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                imageFiles!![file]
                            ),
                            modifier = Modifier
                                .height(thumbnailHeight)
                                .fillMaxWidth()
                                .clickable {
                                // Launch full screen view for image
                                launchImageFullScreen(context, imageFiles!![file])
                            }
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Videos",
                textAlign = TextAlign.Center,
                fontSize = 24.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (videoFiles != null) {
                    items(videoFiles!!.size) { file ->
                        XVideoViewWithOnClick(
                            uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                videoFiles!![file]
                            ),
                            modifier = Modifier
                                .height(thumbnailHeight)
                                .fillMaxWidth()
                                .clickable {
                                // Launch full screen view for video
                                launchVideoFullScreen(context, videoFiles!![file])
                            }
                        )
                    }
                }
            }
        }
    }
}

// Hakee sovelluksella otetut kuvatiedostot
fun getImageFiles(context: Context): Array<out File>? {
    val photoDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraX-Image")
    Log.d("Thumbnails", "PhotoDir: $photoDir")
    val imageFiles = photoDir.listFiles();
    if(imageFiles != null)
        Log.d("Thumbnails", "Images: $imageFiles")
    else
        Log.d("Thumbnails", "No images found")

    return imageFiles
}

// Hakee sovelluksella otetut videotiedostot
fun getVideoFiles(context: Context): Array<out File>? {
    val videoDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "CameraX-Video")
    Log.d("Thumbnails", "VideoDir: $videoDir")
    val videoFiles = videoDir.listFiles();
    if(videoFiles != null)
        Log.d("Thumbnails", "Videos: $videoFiles")
    else
        Log.d("Thumbnails", "No videos found")

    return videoFiles
}

@Composable
fun XImageViewWithOnClick(uri: Uri, modifier: Modifier = Modifier) {
    // Replace XImageView with your custom image view composable
    XImageView(uri = uri, modifier = modifier)
}

@Composable
fun XVideoViewWithOnClick(uri: Uri, modifier: Modifier = Modifier) {
    // Replace XVideoView with your custom video view composable
    XVideoView(uri = uri, modifier = modifier)
}

fun launchImageFullScreen(context: Context, file: File) {
    val intent = Intent(context, ImageFullScreenActivity::class.java)
    intent.putExtra("uri", FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
    context.startActivity(intent)
}

fun launchVideoFullScreen(context: Context, file: File) {
    val intent = Intent(context, VideoFullScreenActivity::class.java)
    intent.putExtra("uri", FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
    context.startActivity(intent)
}

