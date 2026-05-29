package com.example.pierwszaaplikacja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailDetailsScreen
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailShow
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailList
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailScreen
import com.example.pierwszaaplikacja.ui.theme.TrasyTheme
import com.example.pierwszaaplikacja.viewmodel.TrailDetailsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrasyTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    val useSplitPaneMode = booleanResource(id = R.bool.use_split_pane_mode)
    val trailDetailsViewModel: TrailDetailsViewModel = viewModel()
    var selectedTrailId by rememberSaveable { mutableStateOf<Int?>(null) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                            painter = painterResource(id = R.drawable.logo_aplikacji_trasy),
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
                    NavigationDrawerItem(
                        label = { Text("Trasy") },
                        selected = true,
                        onClick = { scope.launch { drawerState.close() } }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
            if (useSplitPaneMode) {
                Row(modifier = contentModifier.fillMaxSize()) {
                    TrailScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onTrailClick = { selectedTrailId = it },
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )

                    if (selectedTrailId != null) {
                        TrailDetailsScreen(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            viewModel = trailDetailsViewModel,
                            trailId = selectedTrailId,
                            onComeBack = { selectedTrailId = null },
                            showBackButton = false
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            } else {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = TrailList,
                    modifier = contentModifier.fillMaxSize()
                ) {
                    composable<TrailList> {
                        TrailScreen(
                            modifier = Modifier.fillMaxSize(),
                            onMenuClick = { scope.launch { drawerState.open() } }
                        ) {
                            navController.navigate(TrailShow(it))
                        }
                    }
                    composable<TrailShow> { entry ->
                        val trailShow: TrailShow = entry.toRoute()
                        TrailDetailsScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = trailDetailsViewModel,
                            trailId = trailShow.id,
                            onComeBack = {
                                navController.popBackStack()
                            },
                            showBackButton = true
                        )
                    }
                }
            }
        }
    }
}