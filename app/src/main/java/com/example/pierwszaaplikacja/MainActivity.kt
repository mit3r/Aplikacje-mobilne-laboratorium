package com.example.pierwszaaplikacja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailDetailsScreen
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailShow
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailList
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailScreen
import com.example.pierwszaaplikacja.ui.theme.PierwszaAplikacjaTheme
import com.example.pierwszaaplikacja.viewmodel.TrailDetailsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PierwszaAplikacjaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    val useSplitPaneMode = booleanResource(id = R.bool.use_split_pane_mode)
    val trailDetailsViewModel: TrailDetailsViewModel = viewModel()
    var selectedTrailId by rememberSaveable { mutableStateOf<Int?>(null) }

    if (useSplitPaneMode) {
        Row(modifier = modifier.fillMaxSize()) {
            TrailScreen(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
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
            modifier = modifier.fillMaxSize()
        ) {
            composable<TrailList> {
                TrailScreen(modifier = Modifier.fillMaxSize()) {
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