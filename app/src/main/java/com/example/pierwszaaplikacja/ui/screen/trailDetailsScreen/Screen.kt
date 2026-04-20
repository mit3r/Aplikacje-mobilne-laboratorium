package com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(text = "Szczegóły trasy", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (trail == null) {
            item {
                Text(text = "Ładowanie...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            val currentTrail = trail!!
            item {
                Text(
                    text = currentTrail.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Dystans: ${currentTrail.distance} km", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = currentTrail.description, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Stopwatch(
                    modifier = Modifier.fillMaxWidth(),
                    onSave = { time ->
                        viewModel.saveTripLog(time)
                    }
                )
            }

            if (tripLogs.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Historia wypraw",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Przesuń, aby usunąć",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(
                    items = tripLogs,
                    key = { it.id } // Klucz dla poprawnego animowania listy
                ) { log ->
                    SwipeToDeleteContainer(
                        onDelete = { viewModel.deleteTripLog(log) }
                    ) {
                        TripLogItem(log)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            if (showBackButton) {
                Button(
                    onClick = onComeBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Powrót do listy tras")
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
