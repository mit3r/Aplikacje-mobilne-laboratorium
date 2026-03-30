package com.example.pierwszaaplikacja.ui.screen.trailListScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pierwszaaplikacja.viewmodel.TrailListViewModel
import kotlinx.serialization.Serializable

@Serializable
object TrailList

@Composable
fun TrailScreen(
    modifier: Modifier = Modifier,
    viewModel: TrailListViewModel = viewModel(),
    onTrailClick: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchTrails()
    }

    val trails by viewModel.trails.collectAsState()

    Column(modifier = modifier) {
        Text(text = "Trasy w systemie")

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(trails.size) { index ->
                TrailItem(
                    trail = trails[index],
                    modifier = Modifier.fillMaxWidth(),
                    onClickAction = onTrailClick
                )
            }
        }
    }
}
