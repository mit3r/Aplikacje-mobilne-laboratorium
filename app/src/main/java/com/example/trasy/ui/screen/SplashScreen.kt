package com.example.trasy.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trasy.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var showBiegacz by remember { mutableStateOf(true) }
    var animationCount by remember { mutableIntStateOf(0) }
    val maxAnimations = 4 // Liczba zamian przed wejściem do aplikacji

    LaunchedEffect(Unit) {
        while (animationCount < maxAnimations) {
            delay(1000)
            showBiegacz = !showBiegacz
            animationCount++
        }
        delay(500)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Używamy _foreground, ponieważ są to pliki rastrowe, które Compose potrafi wyświetlić.
        AnimatedCard(
            isVisible = showBiegacz,
            imageRes = R.mipmap.biegacz_foreground,
            contentDescription = "Biegacz"
        )
        AnimatedCard(
            isVisible = !showBiegacz,
            imageRes = R.mipmap.rowerzysta_foreground,
            contentDescription = "Rowerzysta"
        )
    }
}

@Composable
fun AnimatedCard(
    isVisible: Boolean,
    imageRes: Int,
    contentDescription: String
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1.2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val opacity by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "opacity"
    )

    if (scale > 0.01f) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = opacity
                },
            shape = RoundedCornerShape(24.dp),
            // Ustawiamy białe tło karty i usuwamy wewnętrzny padding obrazka
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize() // Obrazek wypełnia teraz całą kartę
                )
            }
        }
    }
}
