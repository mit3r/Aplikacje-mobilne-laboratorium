package com.example.pierwszaaplikacja.ui.screen.trailListScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.model.TrailType
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
    
    // Podział na kategorie
    val runningTrails = trails.filter { it.type == TrailType.RUNNING }
    val cyclingTrails = trails.filter { it.type == TrailType.CYCLING }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. KARTA GŁÓWNA - Informacja o aplikacji
        item(span = { GridItemSpan(2) }) {
            MainInfoCard()
        }

        // 2. KATEGORIA: BIEGANIE
        if (runningTrails.isNotEmpty()) {
            item(span = { GridItemSpan(2) }) {
                CategoryHeader("🏃 Bieganie", "Odkryj najlepsze ścieżki biegowe")
            }
            items(runningTrails) { trail ->
                TrailGridItem(trail = trail, onClick = { onTrailClick(trail.id) })
            }
        }

        // 3. KATEGORIA: ROWER
        if (cyclingTrails.isNotEmpty()) {
            item(span = { GridItemSpan(2) }) {
                CategoryHeader("🚴 Rower", "Trasy idealne na dwie ramy")
            }
            items(cyclingTrails) { trail ->
                TrailGridItem(trail = trail, onClick = { onTrailClick(trail.id) })
            }
        }
    }
}

@Composable
fun MainInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Witaj w TrailTracker!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Twoja osobista aplikacja do mierzenia czasu na ulubionych szlakach. Wybierz kategorię poniżej i zacznij przygodę!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun CategoryHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun TrailGridItem(trail: Trail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = trail.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = trail.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${trail.distance} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
