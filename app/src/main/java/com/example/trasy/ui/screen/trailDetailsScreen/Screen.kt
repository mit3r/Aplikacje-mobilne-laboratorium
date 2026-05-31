package com.example.trasy.ui.screen.trailDetailsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.trasy.model.Trail
import com.example.trasy.model.TripLog
import com.example.trasy.ui.component.Stopwatch
import com.example.trasy.ui.component.SwipeToDeleteContainer
import com.example.trasy.ui.screen.trailDetailsScreen.components.TripLogItem
import com.example.trasy.viewmodel.TrailDetailsViewModel
import kotlinx.serialization.Serializable

@Serializable
data class TrailShow(val id: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: TrailDetailsViewModel = viewModel(),
    trailId: Int?,
    onComeBack: () -> Unit,
    showBackButton: Boolean = true
) {
    if (trailId == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Nie znaleziono trasy")
        }
        return
    }

    val trails by viewModel.trails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val tripLogs by viewModel.tripLogs.collectAsState()

    // Ładowanie wszystkich tras przy wejściu lub zmianie trailId
    LaunchedEffect(trailId) {
        viewModel.loadAllTrails(trailId)
    }

    // Inicjalizacja stanu pagera
    val pagerState = rememberPagerState(pageCount = { trails.size })

    // Przewinięcie do wybranej trasy po załadowaniu listy LUB zmianie wybranego ID (np. na tablecie)
    LaunchedEffect(trailId, trails) {
        if (trails.isNotEmpty() && trailId != null) {
            val index = trails.indexOfFirst { it.id == trailId }
            if (index != -1 && pagerState.currentPage != index) {
                pagerState.scrollToPage(index)
            }
        }
    }

    // Synchronizacja aktualnego ID trasy z pagerem dla historii i stopera
    LaunchedEffect(pagerState.currentPage, trails) {
        if (trails.isNotEmpty()) {
            viewModel.updateCurrentTrailId(trails[pagerState.currentPage].id)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły trasy") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onComeBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wróć"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading && trails.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { pageIndex ->
                val currentTrail = trails[pageIndex]
                TrailDetailContent(
                    trail = currentTrail,
                    tripLogs = tripLogs,
                    onSaveLog = { viewModel.saveTripLog(it) },
                    onDeleteLog = { viewModel.deleteTripLog(it) }
                )
            }
        }
    }
}

@Composable
fun TrailDetailContent(
    trail: Trail,
    tripLogs: List<TripLog>,
    onSaveLog: (Long) -> Unit,
    onDeleteLog: (TripLog) -> Unit
) {
    var showStopwatch by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AsyncImage(
                    model = trail.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = trail.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dystans: ${trail.distance} km",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = trail.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (showStopwatch) {
                item {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Stopwatch(
                            modifier = Modifier.fillMaxWidth(),
                            onSave = { time ->
                                onSaveLog(time)
                                showStopwatch = false
                            }
                        )
                    }
                }
            }

            if (tripLogs.isNotEmpty()) {
                item {
                    Text(
                        text = "Historia wypraw",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }

                items(
                    items = tripLogs,
                    key = { it.id }
                ) { log ->
                    Box(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                    ) {
                        SwipeToDeleteContainer(
                            onDelete = { onDeleteLog(log) }
                        ) {
                            TripLogItem(log)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showStopwatch = !showStopwatch },
            icon = { Icon(Icons.Default.Timer, contentDescription = null) },
            text = { Text(if (showStopwatch) "Ukryj stoper" else "Mierz czas") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
