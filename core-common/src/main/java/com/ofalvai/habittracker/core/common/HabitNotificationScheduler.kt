package com.ofalvai.habittracker.core.common

import java.time.LocalTime

interface HabitNotificationScheduler {
    fun scheduleNotification(habitId: Int, habitName: String, habitTime: LocalTime?)
    fun cancelNotification(habitId: Int)
}
