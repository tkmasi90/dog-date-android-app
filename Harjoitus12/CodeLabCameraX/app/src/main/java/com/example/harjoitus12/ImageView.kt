package com.example.harjoitus12

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

// Funktio joka vastaa kuvan näyttämisestä
@Composable
fun XImageView(uri: Uri?, modifier: Modifier) {

    Log.d("TakenImageView", "uri: $uri")

    if (uri != null) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setImageURI(uri)
                }
            },
            update = { imageView ->
                imageView.setImageURI(uri)
            }
        )
    }
}

// Funktio joka vastaa videon näyttämisestä
@Composable
fun XVideoView(uri: Uri?, modifier: Modifier) {
    Log.d("TakenVideoView", "uri: $uri")

    if (uri != null) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                VideoView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setVideoURI(uri)
                    setOnPreparedListener { it.start() }
                }
            },
            update = { videoView ->
                videoView.setVideoURI(uri)
                videoView.start()
            }
        )
    }
}
