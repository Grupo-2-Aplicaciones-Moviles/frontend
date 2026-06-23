package weTech.weRide.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import weTech.weRide.ui.navigation.Screen
import weTech.weRide.ui.screens.main.home.HomeScreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Home Screen with map and nearby vehicles
 */
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        onVehicleClick = { vehicleId ->
            navController.navigate(
                Screen.VehicleDetail.createRoute(vehicleId)
            )
        },
        onNavigateToReservation = { vehicleId ->
            // Navigate to reservation flow
            // For now, navigate to vehicle detail
            navController.navigate(
                Screen.VehicleDetail.createRoute(vehicleId)
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WeRideTheme {
        HomeScreen(
            navController = rememberNavController()
        )
    }
}
