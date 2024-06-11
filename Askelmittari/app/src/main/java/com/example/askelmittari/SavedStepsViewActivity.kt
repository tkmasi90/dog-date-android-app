package com.example.askelmittari

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.askelmittari.ui.theme.AskelmittariTheme

// Activity joka käynnistää SavedStepsView näkymän
class SavedStepsViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: StepCounterViewModel by viewModels()
        super.onCreate(savedInstanceState)
        setContent {
            AskelmittariTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d("ThumbnailView", "onCreate: ")
                    SavedStepsView(viewModel)
                }
            }
        }
    }
}