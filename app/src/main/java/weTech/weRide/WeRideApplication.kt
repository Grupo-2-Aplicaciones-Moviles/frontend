package weTech.weRide

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import weTech.weRide.di.appModule
import weTech.weRide.di.networkModule

/**
 * WeRide Application class
 * Initializes Koin dependency injection
 */
class WeRideApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@WeRideApplication)
            modules(
                listOf(
                    appModule,
                    networkModule
                )
            )
        }
    }
}
