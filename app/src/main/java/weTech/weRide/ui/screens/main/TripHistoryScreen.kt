package weTech.weRide.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Trip History Screen Placeholder
 * TODO: Implement with trip history
 */
@Composable
fun TripHistoryScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Viaje - Trip history",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripHistoryScreenPreview() {
    WeRideTheme {
        TripHistoryScreen()
    }
}
