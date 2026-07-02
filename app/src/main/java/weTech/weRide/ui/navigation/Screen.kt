package weTech.weRide.ui.navigation

/**
 * Navigation Routes for WeRide App
 */
sealed class Screen(val route: String) {
    // Authentication Flow
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Login : Screen("login")
    object Register : Screen("register")
    object Phone : Screen("phone")
    object Verification : Screen("verification/{phone}") {
        fun createRoute(phone: String) = "verification/$phone"
    }
    object ProfileSetup : Screen("profile_setup")

    // Main App
    object Home : Screen("home")
    object Garage : Screen("garage")
    object VehicleDetail : Screen("vehicle_detail/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_detail/$vehicleId"
    }
    object Reservation : Screen("reservation/{vehicleId}") {
        fun createRoute(vehicleId: String) = "reservation/$vehicleId"
    }
    object QRScanner : Screen("qr_scanner/{bookingId}") {
        fun createRoute(bookingId: Long) = "qr_scanner/$bookingId"
    }
    object UnlockStatus : Screen("unlock_status/{bookingId}") {
        fun createRoute(bookingId: Long) = "unlock_status/$bookingId"
    }
    object ActiveTrip : Screen("active_trip/{bookingId}") {
        fun createRoute(bookingId: Long) = "active_trip/$bookingId"
    }
    object TripSummary : Screen("trip_summary/{bookingId}") {
        fun createRoute(bookingId: Long) = "trip_summary/$bookingId"
    }
    object Rating : Screen("rating/{bookingId}") {
        fun createRoute(bookingId: Long) = "rating/$bookingId"
    }
    object TripHistory : Screen("trip_history")
    object Plans : Screen("plans")
    object Payment : Screen("payment/{planId}") {
        fun createRoute(planId: String) = "payment/$planId"
    }
    object Profile : Screen("profile")
    object ProfileEdit : Screen("profile_edit")
    object Settings : Screen("settings")
    object Wallet : Screen("wallet")
    object Help : Screen("help")
    object ScheduledUnlock : Screen("scheduled_unlock/{vehicleId}") {
        fun createRoute(vehicleId: String) = "scheduled_unlock/$vehicleId"
    }
}

/**
 * Bottom Navigation Tabs
 */
enum class BottomNavTab(
    val route: String,
    val title: String,
    val icon: Int // TODO: Replace with actual drawable resources
) {
    HOME("home", "Inicio", android.R.drawable.ic_menu_mapmode),
    GARAGE("garage", "Garaje", android.R.drawable.ic_menu_agenda),
    TRIP("trip_history", "Viaje", android.R.drawable.ic_menu_recent_history),
    PLANS("plans", "Planes", android.R.drawable.ic_menu_my_calendar),
    PROFILE("profile", "Tu", android.R.drawable.ic_menu_info_details)
}
