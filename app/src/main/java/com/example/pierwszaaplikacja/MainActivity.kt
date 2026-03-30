package com.example.pierwszaaplikacja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailDetailsScreen
import com.example.pierwszaaplikacja.ui.screen.trailDetailsScreen.TrailShow
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailList
import com.example.pierwszaaplikacja.ui.screen.trailListScreen.TrailScreen
import com.example.pierwszaaplikacja.ui.theme.PierwszaAplikacjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PierwszaAplikacjaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TrailList) {
        composable<TrailList> {
            TrailScreen(modifier = modifier) {
                navController.navigate(TrailShow(it.id))
            }
        }
        composable<TrailShow> { backStackEntry ->
            val entry: TrailShow = backStackEntry.toRoute()
            TrailDetailsScreen(modifier = modifier, trailId = entry.id) {
                navController.popBackStack()
            }
        }
    }
}