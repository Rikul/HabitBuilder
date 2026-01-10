/*
 * Copyright 2022 Olivér Falvai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ofalvai.habittracker.feature.misc.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofalvai.habittracker.core.common.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppInfo(
    val versionName: String,
    val buildType: String,
    val appId: String,
    val urlPrivacyPolicy: String,
    val urlSourceCode: String,
) {
    val marketUrl = "market://details?id=${appId}"
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    val appInfo: AppInfo,
    private val habitDao: com.ofalvai.habittracker.core.database.HabitDao,
    private val notificationManager: com.ofalvai.habittracker.core.common.HabitNotificationScheduler
) : ViewModel() {

    val crashReportingEnabled = MutableStateFlow(true)
    val notificationsEnabled = MutableStateFlow(true)
    val dynamicColor = appPreferences.dynamicColorEnabled

    init {
        crashReportingEnabled.value = appPreferences.crashReportingEnabled
        notificationsEnabled.value = appPreferences.notificationsEnabled
    }

    fun setCrashReportingEnabled(enabled: Boolean) {
        // Nothing to do other than persisting the preference
        // Bugsnag can't be disabled at runtime, but the next app restart won't initialize it
        appPreferences.crashReportingEnabled = enabled
        crashReportingEnabled.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        appPreferences.notificationsEnabled = enabled
        notificationsEnabled.value = enabled

        viewModelScope.launch {
             val habits = habitDao.getHabits()
             habits.forEach { habit ->
                 if (enabled && habit.notifications_enabled) {
                     notificationManager.scheduleNotification(habit.id, habit.name, habit.time)
                 } else {
                     notificationManager.cancelNotification(habit.id)
                 }
             }
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        appPreferences.setDynamicColorEnabled(enabled)
    }
}