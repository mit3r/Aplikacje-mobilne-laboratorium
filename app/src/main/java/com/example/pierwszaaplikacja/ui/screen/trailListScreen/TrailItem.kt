package com.example.pierwszaaplikacja.ui.screen.trailListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pierwszaaplikacja.model.Trail

@Composable
fun TrailItem(
    trail: Trail,
    modifier: Modifier = Modifier,
    onClickAction: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClickAction(trail.id) }
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .aspectRatio(1.0F)
                .background(Color.LightGray)
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = trail.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${trail.distance} km",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = trail.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}
