package com.ofalvai.habittracker.feature.dashboard.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ofalvai.habittracker.core.model.Habit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

import com.ofalvai.habittracker.core.common.HabitNotificationScheduler
import java.time.LocalTime

class HabitNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : HabitNotificationScheduler {

    override fun scheduleNotification(habitId: Int, habitName: String, habitTime: LocalTime?) {
        if (habitTime == null) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            putExtra(HabitNotificationReceiver.HABIT_ID, habitId)
            putExtra(HabitNotificationReceiver.HABIT_NAME, habitName)
            putExtra(HabitNotificationReceiver.HABIT_TIME_HOUR, habitTime.hour)
            putExtra(HabitNotificationReceiver.HABIT_TIME_MINUTE, habitTime.minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = LocalDateTime.now()
        var alarmTime = now.with(habitTime)
        if (alarmTime.isBefore(now)) {
            alarmTime = alarmTime.plusDays(1)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                pendingIntent
            )
        } catch (e: SecurityException) {
            // TODO: Log exception or handle missing USE_EXACT_ALARM permission
        }
    }

    override fun cancelNotification(habitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Note: Intent extras are intentionally omitted because PendingIntent matching
        // uses Intent.filterEquals() which only compares action, data, type, class, and categories
        // (NOT extras). The habitId requestCode is sufficient to identify the correct PendingIntent.
        val intent = Intent(context, HabitNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId, // Use habitId as requestCode to match the scheduled notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // Convenience method
    fun scheduleNotification(habit: Habit) {
        scheduleNotification(habit.id, habit.name, habit.time)
    }
    
    fun cancelNotification(habit: Habit) {
        cancelNotification(habit.id)
    }
}
