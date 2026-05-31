package com.example.trasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.trasy.ui.screen.SplashScreen
import com.example.trasy.ui.screen.trailDetailsScreen.TrailDetailsScreen
import com.example.trasy.ui.screen.trailDetailsScreen.TrailShow
import com.example.trasy.ui.screen.trailListScreen.TrailList
import com.example.trasy.ui.screen.trailListScreen.TrailScreen
import com.example.trasy.ui.theme.TrasyTheme
import com.example.trasy.viewmodel.TrailDetailsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrasyTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onAnimationFinished = { showSplash = false })
                } else {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {
    val useSplitPaneMode = booleanResource(id = R.bool.use_split_pane_mode)
    val trailDetailsViewModel: TrailDetailsViewModel = viewModel()
    var selectedTrailId by rememberSaveable { mutableStateOf<Int?>(null) }
    var currentSectionIndex by rememberSaveable { mutableIntStateOf(0) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    val sections = listOf(
                        "Wszystkie" to Icons.AutoMirrored.Filled.List,
                        "Bieganie" to Icons.AutoMirrored.Filled.DirectionsRun,
                        "Rower" to Icons.Default.PedalBike,
                        "Hiking" to Icons.AutoMirrored.Filled.DirectionsWalk
                    )

                    sections.forEachIndexed { index, (label, icon) ->
                        NavigationDrawerItem(
                            label = { Text(label) },
                            icon = { Icon(icon, contentDescription = null) },
                            selected = currentSectionIndex == index,
                            onClick = { 
                                currentSectionIndex = index
                                if (!useSplitPaneMode) {
                                    navController.navigate(TrailList) {
                                        popUpTo(TrailList) { inclusive = true }
                                    }
                                }
                                scope.launch { drawerState.close() } 
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (useSplitPaneMode) {
                Row(modifier = Modifier.fillMaxSize()) {
                    TrailScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        selectedTrailId = selectedTrailId,
                        selectedCategoryIndex = currentSectionIndex,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onTrailClick = { selectedTrailId = it }
                    )

                    if (selectedTrailId != null) {
                        TrailDetailsScreen(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            viewModel = trailDetailsViewModel,
                            trailId = selectedTrailId,
                            onComeBack = { selectedTrailId = null },
                            showBackButton = false,
                            onTrailChange = { selectedTrailId = it }
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            } else {
                NavHost(
                    navController = navController,
                    startDestination = TrailList,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<TrailList> {
                        TrailScreen(
                            modifier = Modifier.fillMaxSize(),
                            selectedCategoryIndex = currentSectionIndex,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onTrailClick = { trailId ->
                                navController.navigate(TrailShow(trailId))
                            }
                        )
                    }
                    composable<TrailShow> { entry ->
                        val trailShow: TrailShow = entry.toRoute()
                        TrailDetailsScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = trailDetailsViewModel,
                            trailId = trailShow.id,
                            onComeBack = { navController.popBackStack() },
                            showBackButton = true
                        )
                    }
                }
            }
        }
    }
}
