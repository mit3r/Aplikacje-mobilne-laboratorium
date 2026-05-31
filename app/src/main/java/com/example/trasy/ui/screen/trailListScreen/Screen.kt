package com.example.trasy.ui.screen.trailListScreen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trasy.model.Trail
import com.example.trasy.model.TrailType
import com.example.trasy.ui.screen.trailListScreen.components.MainInfoCard
import com.example.trasy.ui.screen.trailListScreen.components.TrailGridItem
import com.example.trasy.viewmodel.TrailListViewModel
import kotlinx.serialization.Serializable

@Serializable
object TrailList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailScreen(
    modifier: Modifier = Modifier,
    viewModel: TrailListViewModel = viewModel(),
    selectedTrailId: Int? = null,
    selectedCategoryIndex: Int = 0,
    onMenuClick: () -> Unit,
    onTrailClick: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchTrails()
    }

    val trails by viewModel.trails.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    // Resetowanie pozycji listy przy zmianie kategorii
    LaunchedEffect(selectedCategoryIndex) {
        if (trails.isNotEmpty()) {
            gridState.animateScrollToItem(0)
        }
    }

    // Filtrowanie: Wyszukiwanie ignoruje kategorie (szuka we wszystkich).
    // Jeśli wyszukiwanie jest puste, filtrujemy po kategorii z szuflady.
    val filteredTrails = remember(trails, selectedCategoryIndex, searchQuery) {
        if (searchQuery.isNotBlank()) {
            trails // ViewModel już przefiltrował globalnie po tekście
        } else {
            when (selectedCategoryIndex) {
                1 -> trails.filter { it.type == TrailType.RUNNING }
                2 -> trails.filter { it.type == TrailType.CYCLING }
                3 -> trails.filter { it.type == TrailType.HIKING }
                else -> trails
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            fadeIn() + slideInHorizontally { it / 2 } togetherWith
                                    fadeOut() + slideOutHorizontally { -it / 2 }
                        },
                        label = "SearchAnimation"
                    ) { active ->
                        if (active) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChange(it) },
                                placeholder = {
                                    Text(
                                        "Szukaj tras...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Image(
                                        painter = painterResource(id = com.example.trasy.R.drawable.ic_launcher_foreground),
                                        contentDescription = null,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(stringResource(id = com.example.trasy.R.string.app_name))
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            isSearchActive = false
                            viewModel.onSearchQueryChange("")
                        } else {
                            isSearchActive = true
                        }
                    }) {
                        AnimatedContent(
                            targetState = isSearchActive,
                            transitionSpec = {
                                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                            },
                            label = "SearchIconAnimation"
                        ) { active ->
                            if (active) {
                                Icon(Icons.Default.Close, contentDescription = "Zamknij wyszukiwanie")
                            } else {
                                Icon(Icons.Default.Search, contentDescription = "Szukaj")
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedCategoryIndex to searchQuery.isBlank(),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.padding(innerPadding),
            label = "CategoryChangeAnimation"
        ) { (categoryIndex, isSearchBlank) ->
            TrailGridContent(
                gridState = gridState,
                trails = filteredTrails,
                selectedTrailId = selectedTrailId,
                showMainCard = categoryIndex == 0 && isSearchBlank,
                onTrailClick = onTrailClick
            )
        }
    }
}

@Composable
fun TrailGridContent(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    trails: List<Trail>,
    selectedTrailId: Int? = null,
    showMainCard: Boolean,
    onTrailClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showMainCard) {
            item(span = { GridItemSpan(2) }) {
                MainInfoCard()
            }
        }

        items(trails, key = { it.id }) { trail ->
            TrailGridItem(
                modifier = Modifier.animateItem(),
                trail = trail,
                isSelected = trail.id == selectedTrailId,
                onClick = { onTrailClick(trail.id) }
            )
        }
    }
}
