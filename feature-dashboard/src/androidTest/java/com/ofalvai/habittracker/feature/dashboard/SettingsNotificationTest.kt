/*
 * Copyright 2025 Olivér Falvai
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

package com.ofalvai.habittracker.feature.dashboard

import android.content.SharedPreferences
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ofalvai.habittracker.core.common.AppPreferences
import com.ofalvai.habittracker.core.testing.BaseInstrumentedTest
import com.ofalvai.habittracker.core.ui.theme.PreviewTheme
import com.ofalvai.habittracker.feature.misc.settings.AppInfo
import com.ofalvai.habittracker.feature.misc.settings.SettingsScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsNotificationTest : BaseInstrumentedTest() {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenLaunched_ThenNotificationToggleIsVisible() {
        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = true,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = {},
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        composeRule.onNodeWithText("Enable Notifications").assertIsDisplayed()
    }

    @Test
    fun givenNotificationsEnabled_WhenLaunched_ThenToggleIsOn() {
        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = true,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = {},
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).assertIsOn()
    }

    @Test
    fun givenNotificationsDisabled_WhenLaunched_ThenToggleIsOff() {
        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = false,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = {},
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).assertIsOff()
    }

    @Test
    fun whenToggleClicked_ThenCallbackInvoked() {
        var callbackInvoked = false
        var callbackValue: Boolean? = null

        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = true,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = { enabled ->
                        callbackInvoked = true
                        callbackValue = enabled
                    },
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        // Click the switch
        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).performClick()

        assertTrue(callbackInvoked)
        assertEquals(false, callbackValue)
    }

    @Test
    fun givenHabitsWithNotifications_WhenToggleDisabled_ThenCallbackReceivesFalse() {
        var capturedValue: Boolean? = null

        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = true,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = { capturedValue = it },
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        // Click the toggle to disable
        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).performClick()

        // Verify callback received false
        assertEquals(false, capturedValue)
    }

    @Test
    fun givenNotificationsDisabled_WhenToggleClicked_ThenCallbackReceivesTrue() {
        var capturedValue: Boolean? = null

        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = false,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = { capturedValue = it },
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        // Click the toggle to enable
        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).performClick()

        // Verify callback received true
        assertEquals(true, capturedValue)
    }

    @Test
    fun givenFirstRun_WhenDisplayed_ThenNotificationsEnabledByDefault() {
        // Clear the notifications_enabled preference to simulate first app run
        sharedPreferences.edit().remove("notifications_enabled").commit()

        // Read the default value - should be true (as defined in AppPreferences)
        val defaultValue = appPreferences.notificationsEnabled

        composeRule.setContent {
            PreviewTheme {
                SettingsScreen(
                    appInfo = AppInfo("1.0", "debug", "com.test", "", ""),
                    crashReportingEnabled = true,
                    notificationsEnabled = defaultValue,
                    dynamicColorEnabled = false,
                    onBack = {},
                    onRateClick = {},
                    onSourceClick = {},
                    onLicensesClick = {},
                    onPrivacyClick = {},
                    onCrashReportingChange = {},
                    onNotificationsChange = {},
                    onDynamicColorChange = {},
                    debugSettings = {}
                )
            }
        }

        // Verify default value is true (as defined in AppPreferences.kt line 67)
        assertTrue("Notifications should be enabled by default on first run", defaultValue)

        // Verify toggle is displayed as ON in the UI
        composeRule.onNode(
            hasParent(hasText("Enable Notifications")) and hasClickAction()
        ).assertIsOn()
    }

}
