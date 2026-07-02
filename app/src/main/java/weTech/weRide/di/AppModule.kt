package weTech.weRide.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import weTech.weRide.data.auth.AuthStateManager
import weTech.weRide.data.repository.AuthRepository
import weTech.weRide.data.repository.BookingRepository
import weTech.weRide.data.repository.VehicleRepository
import weTech.weRide.ui.screens.activeTrip.ActiveTripViewModel
import weTech.weRide.ui.screens.auth.AuthViewModel
import weTech.weRide.ui.screens.editProfile.EditProfileViewModel
import weTech.weRide.ui.screens.main.ProfileViewModel
import weTech.weRide.ui.screens.main.garage.GarageViewModel
import weTech.weRide.ui.screens.main.home.HomeViewModel
import weTech.weRide.ui.screens.main.tripHistory.TripHistoryViewModel
import weTech.weRide.ui.screens.payment.PaymentViewModel
import weTech.weRide.ui.screens.qrscanner.QRScannerViewModel
import weTech.weRide.ui.screens.rating.RatingViewModel
import weTech.weRide.ui.screens.reservation.ReservationViewModel
import weTech.weRide.ui.screens.scheduledBooking.ScheduledBookingViewModel
import weTech.weRide.ui.screens.tripSummary.TripSummaryViewModel
import weTech.weRide.ui.screens.vehicle.VehicleDetailViewModel
import weTech.weRide.ui.screens.wallet.WalletViewModel

/**
 * Application module for Koin Dependency Injection
 */
val appModule = module {
    // Repositories
    single { AuthRepository(get(), get()) }
    single { VehicleRepository(get()) }
    single { BookingRepository(get(), get()) }

    // Auth State Manager
    single { AuthStateManager(get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { GarageViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { VehicleDetailViewModel(get(), get()) }
    viewModel { ReservationViewModel(get(), get(), get()) }
    viewModel { ScheduledBookingViewModel(get(), get(), get(), get()) }
    viewModel { ActiveTripViewModel(get(), get()) }
    viewModel { TripSummaryViewModel(get(), get()) }
    viewModel { RatingViewModel(get(), get()) }
    viewModel { PaymentViewModel(get<String>()) }
    viewModel { QRScannerViewModel() }
    viewModel { WalletViewModel(get()) }
    viewModel { TripHistoryViewModel(get(), get()) }
}
