package com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pierwszaaplikacja.viewmodel.TrailDetailsViewModel
import kotlinx.serialization.Serializable

@Serializable
data class TrailShow(val id: Int)

@Composable
fun TrailDetailsScreen(
    modifier: Modifier = Modifier,

    viewModel: TrailDetailsViewModel = viewModel(),
    trailId: Int,
    onComeBack: () -> Unit
) {
    LaunchedEffect("trailId") { viewModel.fetchTrail(trailId) }

    val trail = viewModel.trail.collectAsState().value

    Column(modifier = modifier) {
        Text(text = "Szczegóły trasy")

        if (trail == null) {
            Text(text = "Ładowanie...")
        } else {
            Text(text = "Tytył (${trail.id}):${trail.title}")
            Text(text = trail.description)
        }

        Button(onClick = onComeBack) {
            Text(text = "Powrót do listy tras")
        }
    }
}