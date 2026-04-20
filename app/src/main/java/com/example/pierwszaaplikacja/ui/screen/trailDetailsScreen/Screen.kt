package com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pierwszaaplikacja.model.TripLog
import com.example.pierwszaaplikacja.ui.component.Stopwatch
import com.example.pierwszaaplikacja.viewmodel.TrailDetailsViewModel
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

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
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = "Szczegóły trasy", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Wybierz trasę z listy", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LaunchedEffect(trailId) { viewModel.fetchTrail(trailId) }

    val trail by viewModel.trail.collectAsState()
    val tripLogs by viewModel.tripLogs.collectAsState()
    
    // Stan dla stopera wywoływanego z FAB
    var showStopwatch by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showBackButton) {
                TopAppBar(
                    title = { Text(trail?.title ?: "Szczegóły") },
                    navigationIcon = {
                        IconButton(onClick = onComeBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (trail != null) {
                ExtendedFloatingActionButton(
                    onClick = { showStopwatch = !showStopwatch },
                    icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    text = { Text(if (showStopwatch) "Ukryj stoper" else "Mierz czas") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (trail == null) {
                item {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                val currentTrail = trail!!
                item {
                    AsyncImage(
                        model = currentTrail.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentTrail.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Dystans: ${currentTrail.distance} km",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTrail.description,
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
                                    viewModel.saveTripLog(time)
                                    showStopwatch = false // Ukryj po zapisaniu
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
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    items(
                        items = tripLogs,
                        key = { it.id }
                    ) { log ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteTripLog(log) }
                            ) {
                                TripLogItem(log)
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Miejsce na FAB
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                    else -> MaterialTheme.colorScheme.errorContainer
                }, label = "background"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) 
                    Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = { content() }
    )
}

@Composable
fun TripLogItem(log: TripLog) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(log.date))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Data: $dateString", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Czas: ${formatTime(log.timeInSeconds)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
}
