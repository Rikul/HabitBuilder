package com.ofalvai.habittracker.feature.dashboard.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ofalvai.habittracker.feature.dashboard.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.random.Random

class HabitNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val HABIT_ID = "habit_id"
        const val HABIT_NAME = "habit_name"
        const val HABIT_TIME_HOUR = "habit_time_hour"
        const val HABIT_TIME_MINUTE = "habit_time_minute"
        const val CHANNEL_ID = "habit_reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getIntExtra(HABIT_ID, -1)
        val habitName = intent.getStringExtra(HABIT_NAME) ?: return
        val habitTimeHour = intent.getIntExtra(HABIT_TIME_HOUR, -1)
        val habitTimeMinute = intent.getIntExtra(HABIT_TIME_MINUTE, -1)

        if (habitId == -1 || habitTimeHour == -1 || habitTimeMinute == -1) return

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dashboard_layout)
            .setContentTitle("Habit Reminder")
            .setContentText(habitName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= 33) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                 NotificationManagerCompat.from(context).notify(habitId, notification)
            }
        } else {
             NotificationManagerCompat.from(context).notify(habitId, notification)
        }

        // Reschedule for tomorrow at the same time
        rescheduleNotification(context, habitId, habitName, habitTimeHour, habitTimeMinute)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Habit Reminders"
            val descriptionText = "Notifications for your scheduled habits"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun rescheduleNotification(
        context: Context,
        habitId: Int,
        habitName: String,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val habitTime = LocalTime.of(hour, minute)

        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            putExtra(HABIT_ID, habitId)
            putExtra(HABIT_NAME, habitName)
            putExtra(HABIT_TIME_HOUR, hour)
            putExtra(HABIT_TIME_MINUTE, minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for tomorrow at the same time
        val now = LocalDateTime.now()
        val nextAlarmTime = now.with(habitTime).plusDays(1)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle missing USE_EXACT_ALARM permission
        }
    }
}
