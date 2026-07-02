package weTech.weRide.di

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import weTech.weRide.data.api.services.BookingApiService
import weTech.weRide.data.api.services.VehicleApiService
import weTech.weRide.data.api.services.AuthApiService
import weTech.weRide.data.pref.TokenManager
import weTech.weRide.utils.AppConfig
import weTech.weRide.utils.Constants
import java.util.concurrent.TimeUnit

/**
 * Network module for Koin Dependency Injection
 * Provides Retrofit, OkHttp, and API services
 */
val networkModule = module {
    // Token Manager
    single { TokenManager(get()) }

    // Logging Interceptor
    single {
        HttpLoggingInterceptor().apply {
            level = if (AppConfig.enableLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // Auth Interceptor
    single {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val tokenManager: TokenManager = get()

            val requestBuilder = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")

            // Add auth token if available (blocking call for simplicity)
            try {
                val token = runBlocking {
                    tokenManager.getToken().first() // Get first emission only
                }
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                // Ignore token errors
            }

            chain.proceed(requestBuilder.build())
        }
    }

    // OkHttp Client
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<Interceptor>()) // Auth interceptor
            .connectTimeout(Constants.TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_WRITE, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance
    single {
        Retrofit.Builder()
            .baseUrl(AppConfig.apiBaseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Services
    single { get<Retrofit>().create(AuthApiService::class.java) }
    single { get<Retrofit>().create(VehicleApiService::class.java) }
    single { get<Retrofit>().create(BookingApiService::class.java) }
}
