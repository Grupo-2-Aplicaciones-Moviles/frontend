package weTech.weRide.utils

object Constants {
    // API Configuration
    // Production backend URL (Render)
    const val BASE_URL = "https://backend-e5km.onrender.com/api/v1/"
    // Local development: http://10.0.2.2:8080/api/v1/ (emulator) or http://192.168.1.38:8080/api/v1/ (device on same network)
    const val TIMEOUT_CONNECT = 30L
    const val TIMEOUT_READ = 30L
    const val TIMEOUT_WRITE = 30L

    // API Endpoints
    const val AUTH_SIGN_IN = "authentication/sign-in"
    const val AUTH_SIGN_UP = "authentication/sign-up"
    const val VEHICLES = "vehicles"
    const val BOOKINGS = "bookings"
    const val BOOKINGS_DRAFT = "bookings/draft"

    // Preferences Keys
    const val PREFS_NAME = "weride_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_PHOTO = "user_photo"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_WALLET_BALANCE = "wallet_balance"
    const val KEY_WALLET_TRANSACTIONS = "wallet_transactions"
    const val KEY_WALLET_LAST_UPDATED = "wallet_last_updated"

    // Navigation Routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_AUTH = "auth"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_PHONE = "phone"
    const val ROUTE_VERIFICATION = "verification"
    const val ROUTE_PROFILE_SETUP = "profile_setup"
    const val ROUTE_HOME = "home"
    const val ROUTE_GARAGE = "garage"
    const val ROUTE_VEHICLE_DETAIL = "vehicle_detail"
    const val ROUTE_RESERVATION = "reservation"
    const val ROUTE_QR_SCANNER = "qr_scanner"
    const val ROUTE_ACTIVE_TRIP = "active_trip"
    const val ROUTE_TRIP_SUMMARY = "trip_summary"
    const val ROUTE_RATING = "rating"
    const val ROUTE_TRIP_HISTORY = "trip_history"
    const val ROUTE_PLANS = "plans"
    const val ROUTE_PAYMENT = "payment"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_PROFILE_EDIT = "profile_edit"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_WALLET = "wallet"
    const val ROUTE_HELP = "help"
    const val ROUTE_SCHEDULED_UNLOCK = "scheduled_unlock"

    // Reservation
    const val RESERVATION_TIME_MINUTES = 15
    const val RESERVATION_WARNING_MINUTES = 2

    // Vehicle Types
    const val VEHICLE_TYPE_SCOOTER = "scooter"
    const val VEHICLE_TYPE_BIKE = "bike"
    const val VEHICLE_TYPE_MOTORCYCLE = "motorcycle"

    // Booking Status
    const val BOOKING_STATUS_DRAFT = "draft"
    const val BOOKING_STATUS_CONFIRMED = "confirmed"
    const val BOOKING_STATUS_ACTIVE = "active"
    const val BOOKING_STATUS_COMPLETED = "completed"
    const val BOOKING_STATUS_CANCELLED = "cancelled"

    // Rating
    const val MAX_RATING = 5
    const val MIN_RATING = 1

    // Payment Methods
    const val PAYMENT_METHOD_CARD = "card"
    const val PAYMENT_METHOD_YAPE = "yape"
    const val PAYMENT_METHOD_PLIN = "plin"

    // Report Categories
    const val REPORT_MECHANICAL = "Falla mecánica"
    const val REPORT_BATTERY = "Batería"
    const val REPORT_LOCK = "Bloqueo"
    const val REPORT_ACCIDENT = "Accidente"
    const val REPORT_APP = "App"

    // Verification
    const val VERIFICATION_CODE_LENGTH = 6
    const val RESEND_TIMER_SECONDS = 60

    // Phone
    const val PHONE_LENGTH = 9
    const val COUNTRY_CODE_PERU = "+51"
}
