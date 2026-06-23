package weTech.weRide.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import weTech.weRide.MainActivity
import weTech.weRide.R
import weTech.weRide.receiver.BookingReminderReceiver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Notification Scheduler Utility
 * Handles scheduling and displaying notifications for scheduled bookings
 */
class NotificationScheduler(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "weride_booking_notifications"
        private const val CHANNEL_NAME = "Reservas WeRide"
        private const val CHANNEL_DESCRIPTION = "Notificaciones para reservas programadas"

        private const val NOTIFICATION_ID = 1001
        private const val REMINDER_MINUTES_BEFORE = 15

        const val EXTRA_BOOKING_ID = "booking_id"
        const val EXTRA_VEHICLE_BRAND = "vehicle_brand"
        const val EXTRA_VEHICLE_MODEL = "vehicle_model"
        const val EXTRA_MINUTES_BEFORE = "minutes_before"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedule a reminder notification for a booking
     * @param bookingId The ID of the booking
     * @param bookingStartTime The start time of the booking
     * @param vehicleBrand The brand of the vehicle
     * @param vehicleModel The model of the vehicle
     */
    fun scheduleBookingReminder(
        bookingId: Long,
        bookingStartTime: LocalDateTime,
        vehicleBrand: String,
        vehicleModel: String
    ) {
        val reminderTime = bookingStartTime.minusMinutes(REMINDER_MINUTES_BEFORE.toLong())
        val now = LocalDateTime.now()

        // Only schedule if reminder time is in the future
        if (reminderTime.isAfter(now)) {
            val triggerTime = reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val intent = Intent(context, BookingReminderReceiver::class.java).apply {
                putExtra(EXTRA_BOOKING_ID, bookingId)
                putExtra(EXTRA_VEHICLE_BRAND, vehicleBrand)
                putExtra(EXTRA_VEHICLE_MODEL, vehicleModel)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                bookingId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Handle exact alarm permission error
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    /**
     * Cancel a scheduled reminder
     * @param bookingId The ID of the booking
     */
    fun cancelBookingReminder(bookingId: Long) {
        val intent = Intent(context, BookingReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            bookingId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    /**
     * Show immediate notification (for testing or instant alerts)
     * @param title The notification title
     * @param message The notification message
     * @param bookingId The ID of the booking
     */
    fun showImmediateNotification(
        title: String,
        message: String,
        bookingId: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_BOOKING_ID, bookingId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            bookingId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Schedule multiple reminders for a booking
     * @param bookingId The ID of the booking
     * @param bookingStartTime The start time of the booking
     * @param vehicleBrand The brand of the vehicle
     * @param vehicleModel The model of the vehicle
     */
    fun scheduleMultipleReminders(
        bookingId: Long,
        bookingStartTime: LocalDateTime,
        vehicleBrand: String,
        vehicleModel: String
    ) {
        // Schedule reminders at different intervals
        val reminderIntervals = listOf(60, 30, 15) // minutes before

        reminderIntervals.forEach { minutes ->
            val reminderTime = bookingStartTime.minusMinutes(minutes.toLong())
            val now = LocalDateTime.now()

            if (reminderTime.isAfter(now)) {
                val triggerTime = reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val intent = Intent(context, BookingReminderReceiver::class.java).apply {
                    putExtra(EXTRA_BOOKING_ID, bookingId)
                    putExtra(EXTRA_VEHICLE_BRAND, vehicleBrand)
                    putExtra(EXTRA_VEHICLE_MODEL, vehicleModel)
                    putExtra(EXTRA_MINUTES_BEFORE, minutes)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (bookingId + minutes).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        }
    }

    /**
     * Cancel all reminders for a booking
     * @param bookingId The ID of the booking
     */
    fun cancelAllReminders(bookingId: Long) {
        val reminderIntervals = listOf(0, 15, 30, 60) // minutes before

        reminderIntervals.forEach { minutes ->
            val intent = Intent(context, BookingReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (bookingId + minutes).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }
}

/**
 * Broadcast Receiver for booking reminders
 * Note: This should be registered in AndroidManifest.xml
 */
/*
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
*/
