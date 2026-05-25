package com.example.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.CalendarEvent

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_SOUND_ID = "glass_calendar_alerts_sound"
        const val CHANNEL_SILENT_ID = "glass_calendar_alerts_silent"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Sound Channel (High Importance)
            val soundChannel = NotificationChannel(
                CHANNEL_SOUND_ID,
                "Calendar Alerts (Sound)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Custom event reminders with ringtone sounds and banner alerts."
                enableLights(true)
                enableVibration(true)
            }

            // Silent Channel (Low Importance)
            val silentChannel = NotificationChannel(
                CHANNEL_SILENT_ID,
                "Calendar Alerts (Silent)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Quiet calendar updates that only show up as an icon badge."
                enableLights(false)
                enableVibration(false)
            }

            notificationManager.createNotificationChannel(soundChannel)
            notificationManager.createNotificationChannel(silentChannel)
        }
    }

    fun showEventNotification(event: CalendarEvent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to launch our application when the user taps on the calendar notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = if (event.notifyStyle == "SOUND") CHANNEL_SOUND_ID else CHANNEL_SILENT_ID

        val formatTiming = when (event.notifyBeforeMins) {
            -1 -> "Custom Reminder"
            0 -> "Starting now!"
            5 -> "Starting in 5 minutes"
            15 -> "Starting in 15 minutes"
            30 -> "Starting in 30 minutes"
            60 -> "Starting in 1 hour"
            1440 -> "Starting in 1 day"
            else -> "Reminder set for ${event.notifyBeforeMins} min before"
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.sym_def_app_icon) // default launcher app icon fallback
            .setContentTitle("📅 ${event.title}")
            .setContentText("${formatTiming} | Loc: ${event.location.ifEmpty { "Not set" }}")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Event Alert: ${event.title}\n" +
                    "Time: $formatTiming\n" +
                    "Location: ${event.location.ifEmpty { "Not specified" }}\n\n" +
                    "Details:\n${event.description.ifEmpty { "No additional details was provided." }}"
                )
            )
            .setPriority(if (event.notifyStyle == "SOUND") NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Generate a positive unique notification id
        val notificationId = if (event.id == 0L) System.currentTimeMillis().toInt() and 0xffff else event.id.toInt()
        notificationManager.notify(notificationId, builder.build())
    }
}
