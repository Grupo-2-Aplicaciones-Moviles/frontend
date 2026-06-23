package weTech.weRide.utils

/**
 * Application configuration that can be modified at runtime
 */
object AppConfig {
    var apiBaseUrl: String = Constants.BASE_URL
        private set

    var enableLogging: Boolean = true
        private set

    var isDevelopmentMode: Boolean = true
        private set

    fun updateApiUrl(url: String) {
        apiBaseUrl = url
    }

    fun setProductionMode() {
        isDevelopmentMode = false
        enableLogging = false
    }

    fun setDevelopmentMode() {
        isDevelopmentMode = true
        enableLogging = true
    }
}
