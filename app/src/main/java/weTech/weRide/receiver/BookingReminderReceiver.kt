package weTech.weRide.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import weTech.weRide.utils.NotificationScheduler

/**
 * Broadcast Receiver for booking reminders
 * Registered in AndroidManifest.xml to receive scheduled alarms
 */
class BookingReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bookingId = intent.getLongExtra(NotificationScheduler.EXTRA_BOOKING_ID, -1L)
        val vehicleBrand = intent.getStringExtra(NotificationScheduler.EXTRA_VEHICLE_BRAND) ?: ""
        val vehicleModel = intent.getStringExtra(NotificationScheduler.EXTRA_VEHICLE_MODEL) ?: ""
        val minutesBefore = intent.getIntExtra(NotificationScheduler.EXTRA_MINUTES_BEFORE, 15)

        val scheduler = NotificationScheduler(context)

        when (minutesBefore) {
            60 -> {
                scheduler.showImmediateNotification(
                    title = "Tu reserva comienza en 1 hora",
                    message = "El vehículo $vehicleBrand $vehicleModel estará disponible en 60 minutos",
                    bookingId = bookingId
                )
            }
            30 -> {
                scheduler.showImmediateNotification(
                    title = "Tu reserva comienza en 30 minutos",
                    message = "El vehículo $vehicleBrand $vehicleModel estará disponible pronto",
                    bookingId = bookingId
                )
            }
            else -> {
                scheduler.showImmediateNotification(
                    title = "Tu reserva comienza en $minutesBefore minutos",
                    message = "Dirígete al vehículo $vehicleBrand $vehicleModel",
                    bookingId = bookingId
                )
            }
        }
    }
}
