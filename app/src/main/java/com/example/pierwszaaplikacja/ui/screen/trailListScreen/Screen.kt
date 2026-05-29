package com.example.pierwszaaplikacja.ui.screen.trailListScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pierwszaaplikacja.R
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.model.TrailType
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.components.CategoryHeader
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.components.MainInfoCard
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.components.TrailGridItem
import com.example.pierwszaaplikacja.viewmodel.TrailListViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object TrailList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailScreen(
    modifier: Modifier = Modifier,
    viewModel: TrailListViewModel = viewModel(),
    onMenuClick: () -> Unit,
    onTrailClick: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchTrails()
    }

    val trails by viewModel.trails.collectAsState()
    val scope = rememberCoroutineScope()

    // Konfiguracja zakładek i pagera
    val tabs = listOf("Wszystkie", "🏃 Bieganie", "🚴 Rower")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_aplikacji_trasy),
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = R.string.app_name))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
                // TabRow zintegrowany z pagerState
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // HorizontalPager umożliwia gest przeciągnięcia (swipe)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = androidx.compose.ui.Alignment.Top
        ) { pageIndex ->
            // Filtrowanie listy w zależności od aktywnej karty
            val filteredTrails = when (pageIndex) {
                1 -> trails.filter { it.type == TrailType.RUNNING }
                2 -> trails.filter { it.type == TrailType.CYCLING }
                else -> trails
            }

            TrailGridContent(
                trails = filteredTrails,
                showMainCard = pageIndex == 0, // Pokazuj info-kartę tylko na pierwszej stronie
                activeType = when (pageIndex) {
                    1 -> TrailType.RUNNING
                    2 -> TrailType.CYCLING
                    else -> null
                },
                onTrailClick = onTrailClick
            )
        }
    }
}

@Composable
fun TrailGridContent(
    trails: List<Trail>,
    showMainCard: Boolean,
    activeType: TrailType?,
    onTrailClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showMainCard) {
            item(span = { GridItemSpan(2) }) {
                MainInfoCard()
            }
        }

        // Sekcja: Bieganie (jeśli nie filtrujemy tylko pod rower)
        val running = trails.filter { it.type == TrailType.RUNNING }
        if (running.isNotEmpty() && (activeType == null || activeType == TrailType.RUNNING)) {
            item(span = { GridItemSpan(2) }) {
                CategoryHeader("🏃 Bieganie", "Najlepsze ścieżki w okolicy")
            }
            items(running) { trail ->
                TrailGridItem(
                    trail = trail,
                    onClick = { onTrailClick(trail.id) })
            }
        }

        // Sekcja: Rower (jeśli nie filtrujemy tylko pod bieganie)
        val cycling = trails.filter { it.type == TrailType.CYCLING }
        if (cycling.isNotEmpty() && (activeType == null || activeType == TrailType.CYCLING)) {
            item(span = { GridItemSpan(2) }) {
                CategoryHeader("🚴 Rower", "Trasy rowerowe i MTB")
            }
            items(cycling) { trail ->
                TrailGridItem(
                    trail = trail,
                    onClick = { onTrailClick(trail.id) })
            }
        }
    }
}
