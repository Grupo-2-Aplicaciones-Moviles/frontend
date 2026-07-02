package weTech.weRide.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import weTech.weRide.ui.screens.activeTrip.ActiveTripScreen
import weTech.weRide.ui.screens.auth.AuthScreen
import weTech.weRide.ui.screens.editProfile.EditProfileScreen
import weTech.weRide.ui.screens.payment.PaymentScreen
import weTech.weRide.ui.screens.qrscanner.QRScannerScreen
import weTech.weRide.ui.screens.rating.TripRatingScreen
import weTech.weRide.ui.screens.scheduledBooking.ScheduledBookingScreen
import weTech.weRide.ui.screens.auth.PhoneScreen
import weTech.weRide.ui.screens.auth.ProfileSetupScreen
import weTech.weRide.ui.screens.auth.SplashScreen
import weTech.weRide.ui.screens.auth.VerificationScreen
import weTech.weRide.ui.screens.main.GarageScreen
import weTech.weRide.ui.screens.main.HomeScreen
import weTech.weRide.ui.screens.main.PlansScreen
import weTech.weRide.ui.screens.main.ProfileScreen
import weTech.weRide.ui.screens.main.TripHistoryScreen
import weTech.weRide.ui.screens.reservation.ReservationScreen
import weTech.weRide.ui.screens.settings.SettingsScreen
import weTech.weRide.ui.screens.tripSummary.TripSummaryScreen
import weTech.weRide.ui.screens.vehicle.VehicleDetailScreen
import weTech.weRide.ui.screens.wallet.WalletScreen
import weTech.weRide.ui.theme.WeRideTheme

/**
 * Main Navigation Graph for WeRide App
 */
@Composable
fun WeRideNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Flow
        addAuthGraph(navController)

        // Main App Screens
        addMainAppGraph(navController)
    }
}

/**
 * Authentication Flow Navigation Graph
 */
private fun NavGraphBuilder.addAuthGraph(navController: NavHostController) {
    // Splash Screen
    composable(Screen.Splash.route) {
        SplashScreen(
            onNavigateToAuth = {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            },
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        )
    }

    // Auth Screen (Login/Register)
    composable(Screen.Auth.route) {
        AuthScreen(
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        )
    }

    // Phone Number Screen
    composable(Screen.Phone.route) {
        PhoneScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToVerification = { phone ->
                navController.navigate(Screen.Verification.createRoute(phone))
            }
        )
    }

    // Verification Code Screen
    composable(
        route = Screen.Verification.route,
        arguments = listOf(
            navArgument("phone") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val phone = backStackEntry.arguments?.getString("phone") ?: ""
        VerificationScreen(
            phoneNumber = phone,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToProfileSetup = {
                navController.navigate(Screen.ProfileSetup.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
        )
    }

    // Profile Setup Screen
    composable(Screen.ProfileSetup.route) {
        ProfileSetupScreen(
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        )
    }
}

/**
 * Main App Navigation Graph with Bottom Navigation
 */
private fun NavGraphBuilder.addMainAppGraph(navController: NavHostController) {

    // Home Screen
    composable(Screen.Home.route) {
        MainAppWrapper(
            selectedTab = BottomNavTab.HOME,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            HomeScreen(navController = navController)
        }
    }

    // Garage Screen
    composable(Screen.Garage.route) {
        MainAppWrapper(
            selectedTab = BottomNavTab.GARAGE,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            GarageScreen(navController = navController)
        }
    }

    // Trip History Screen
    composable(Screen.TripHistory.route) {
        MainAppWrapper(
            selectedTab = BottomNavTab.TRIP,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            TripHistoryScreen()
        }
    }

    // Plans Screen
    composable(Screen.Plans.route) {
        MainAppWrapper(
            selectedTab = BottomNavTab.PLANS,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            PlansScreen()
        }
    }

    // Profile Screen
    composable(Screen.Profile.route) {
        MainAppWrapper(
            selectedTab = BottomNavTab.PROFILE,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            ProfileScreen(navController = navController)
        }
    }

    // Vehicle Detail Screen
    composable(
        route = Screen.VehicleDetail.route,
        arguments = listOf(
            navArgument("vehicleId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
        VehicleDetailScreen(navController = navController)
    }

    // Reservation Screen
    composable(
        route = Screen.Reservation.route,
        arguments = listOf(
            navArgument("vehicleId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
        ReservationScreen(
            navController = navController,
            vehicleId = vehicleId
        )
    }

    // QR Scanner Screen
    composable(
        route = Screen.QRScanner.route,
        arguments = listOf(
            navArgument("bookingId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
        QRScannerScreen(
            navController = navController,
            bookingId = bookingId
        )
    }

    // Active Trip Screen
    composable(
        route = Screen.ActiveTrip.route,
        arguments = listOf(
            navArgument("bookingId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
        ActiveTripScreen(
            navController = navController,
            bookingId = bookingId
        )
    }

    // Trip Summary Screen
    composable(
        route = Screen.TripSummary.route,
        arguments = listOf(
            navArgument("bookingId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
        TripSummaryScreen(
            navController = navController,
            bookingId = bookingId
        )
    }

    // Rating Screen
    composable(
        route = Screen.Rating.route,
        arguments = listOf(
            navArgument("bookingId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
        TripRatingScreen(
            navController = navController,
            bookingId = bookingId
        )
    }

    // Payment Screen
    composable(
        route = Screen.Payment.route,
        arguments = listOf(
            navArgument("planId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val planId = backStackEntry.arguments?.getString("planId") ?: ""
        PaymentScreen(
            navController = navController,
            planId = planId
        )
    }

    // Settings Screen
    composable(Screen.Settings.route) {
        SettingsScreen(navController = navController)
    }

    // Wallet Screen
    composable(Screen.Wallet.route) {
        WalletScreen(navController = navController)
    }

    // Scheduled Booking Screen
    composable(
        route = Screen.ScheduledUnlock.route,
        arguments = listOf(
            navArgument("vehicleId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
        ScheduledBookingScreen(
            navController = navController,
            vehicleId = vehicleId
        )
    }

    // Help Screen (placeholder)
    composable(Screen.Help.route) {
        HelpScreen(navController = navController)
    }

    // Profile Edit Screen
    composable(Screen.ProfileEdit.route) {
        EditProfileScreen(navController = navController)
    }
}

/**
 * Help Screen Placeholder
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Centro de ayuda - Próximamente")
        }
    }
}

/**
 * Wrapper for main app screens with bottom navigation
 */
@Composable
private fun MainAppWrapper(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    content: @Composable () -> Unit
) {
    weTech.weRide.ui.components.WeRideScaffold(
        bottomBar = {
            weTech.weRide.ui.components.WeRideBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) {
        content()
    }
}
