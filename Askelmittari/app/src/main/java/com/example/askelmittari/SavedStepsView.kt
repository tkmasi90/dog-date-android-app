package com.example.askelmittari

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tallennettujen askelten näkymä
@Composable
fun SavedStepsView(viewModel: StepCounterViewModel) {
    val steps by viewModel.stepHistory.collectAsState()

    Column {
        LazyColumn(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(0.9f),
            contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(steps.size) { step ->
                Text(text = stringResource(R.string.date_history, steps[step].first),
                    fontSize = 20.sp)
                Text(text = stringResource(R.string.steps_history, steps[step].second),
                    fontSize = 20.sp)
            }
        }
        FilledIconButton(
            onClick = { viewModel.emptyHistory() },
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(4.dp)) // Add some vertical space between the icon and text
                Text(
                    text = stringResource(R.string.empty_history),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

