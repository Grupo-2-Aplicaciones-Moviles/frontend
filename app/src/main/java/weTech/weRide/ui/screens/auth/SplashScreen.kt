package weTech.weRide.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import weTech.weRide.R
import weTech.weRide.ui.theme.Black
import weTech.weRide.ui.theme.WeRideTheme
import weTech.weRide.ui.theme.White

/**
 * Splash Screen
 * Shows WeRide logo on black background
 */
@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Simulate loading check
        delay(500)
        showContent = true

        // TODO: Check if user is logged in
        // For now, always navigate to auth
        delay(2000)
        onNavigateToAuth()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo (scooter icon placeholder)
            androidx.compose.foundation.Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "WeRide Logo",
                modifier = Modifier.size(120.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "WeRide",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Movilidad eléctrica inteligente",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    WeRideTheme {
        SplashScreen(
            onNavigateToAuth = {},
            onNavigateToHome = {}
        )
    }
}
