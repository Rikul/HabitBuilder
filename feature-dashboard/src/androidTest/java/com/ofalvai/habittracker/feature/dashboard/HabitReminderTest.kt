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

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.ofalvai.habittracker.core.model.Habit
import com.ofalvai.habittracker.core.testing.BaseInstrumentedTest
import com.ofalvai.habittracker.core.ui.theme.PreviewTheme
import com.ofalvai.habittracker.feature.dashboard.ui.addhabit.AddHabitForm
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HabitReminderTest : BaseInstrumentedTest() {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    /**
     * Helper function to handle POST_NOTIFICATIONS permission dialog on Android 13+.
     * Call this BEFORE clicking the reminder checkbox in tests that need to set reminders.
     *
     * This function:
     * - Does nothing on Android < 13 (permission doesn't exist)
     * - Looks for the system permission dialog
     * - Clicks "Allow" if the dialog appears
     * - Returns immediately if dialog doesn't appear (permission already granted)
     */
    private fun grantNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < 33) {
            // Permission only exists on Android 13+
            return
        }

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Look for common text patterns in permission dialogs across different Android versions/OEMs
        // Try multiple selectors to handle variations
        val allowButton = device.findObject(
            UiSelector()
                .textMatches("(?i)(allow|permit|ok)")
                .clickable(true)
        )

        // Wait briefly for dialog to appear (500ms is enough)
        if (allowButton.waitForExists(500)) {
            // Dialog appeared, click Allow
            allowButton.click()
            // Give the system time to process the permission grant
            Thread.sleep(300)
        }
        // If dialog doesn't appear, permission is already granted - continue
    }

    @Test
    fun whenNoTimeSet_ThenReminderCheckboxNotVisible() {
        val onSave: (Habit) -> Unit = {}

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave)
            }
        }

        composeRule.onNodeWithText("Set Reminder").assertDoesNotExist()
    }

    @Test
    fun givenTimeSet_WhenLaunched_ThenReminderCheckboxIsVisible() {
        val onSave: (Habit) -> Unit = {}
        val initialTime = LocalTime.of(14, 30)

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave, initialTime)
            }
        }

        composeRule.onNodeWithText("Set Reminder").assertIsDisplayed()
    }

    @Test
    fun givenTimeSet_WhenTimeClearedViaButton_ThenReminderCheckboxDisappears() {
        val onSave: (Habit) -> Unit = {}
        val initialTime = LocalTime.of(14, 30)

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave, initialTime)
            }
        }

        // Verify reminder checkbox is visible
        composeRule.onNodeWithText("Set Reminder").assertIsDisplayed()

        // Clear the time
        composeRule.onNodeWithContentDescription("Clear").performClick()

        // Verify reminder checkbox is gone
        composeRule.onNodeWithText("Set Reminder").assertDoesNotExist()
    }

    @Test
    fun whenReminderChecked_ThenSavedHabitHasNotificationsEnabled() {
        var savedHabit: Habit? = null
        val onSave: (Habit) -> Unit = { savedHabit = it }
        val initialTime = LocalTime.of(9, 0)

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave, initialTime)
            }
        }

        // Check the reminder checkbox
        composeRule.onNode(
            isToggleable() and hasAnySibling(hasText("Set Reminder"))
        ).performClick()
        grantNotificationPermissionIfNeeded()

        // Enter habit name and save
        composeRule.onNodeWithText("Habit name").performTextInput("Morning Meditation")
        composeRule.onNodeWithText("Create habit").performClick()

        // Verify saved habit has time and notifications enabled
        assertEquals(initialTime, savedHabit?.time)
        assertTrue(savedHabit?.notificationsEnabled ?: false)
    }

    @Test
    fun whenReminderUnchecked_ThenSavedHabitHasNotificationsDisabled() {
        var savedHabit: Habit? = null
        val onSave: (Habit) -> Unit = { savedHabit = it }
        val initialTime = LocalTime.of(9, 0)

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave, initialTime)
            }
        }

        // Don't check the reminder checkbox - leave it unchecked

        // Enter habit name and save
        composeRule.onNodeWithText("Habit name").performTextInput("Morning Meditation")
        composeRule.onNodeWithText("Create habit").performClick()

        // Verify saved habit has time but notifications disabled
        assertEquals(initialTime, savedHabit?.time)
        assertFalse(savedHabit?.notificationsEnabled ?: true)
    }

    @Test
    fun givenReminderChecked_WhenUnchecked_ThenSavedHabitHasNotificationsDisabled() {
        var savedHabit: Habit? = null
        val onSave: (Habit) -> Unit = { savedHabit = it }
        val initialTime = LocalTime.of(9, 0)

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave, initialTime)
            }
        }

        // Check the reminder checkbox
        val checkboxNode = composeRule.onNode(
            isToggleable() and hasAnySibling(hasText("Set Reminder"))
        )
        checkboxNode.performClick()

        grantNotificationPermissionIfNeeded()

        // Uncheck the reminder checkbox
        checkboxNode.performClick()

        // Enter habit name and save
        composeRule.onNodeWithText("Habit name").performTextInput("Morning Meditation")
        composeRule.onNodeWithText("Create habit").performClick()

        // Verify saved habit has time but notifications disabled
        assertEquals(initialTime, savedHabit?.time)
        assertFalse(savedHabit?.notificationsEnabled ?: true)
    }

    @Test
    fun givenNoInitialTime_WhenTimeSetAndReminderChecked_ThenSavedCorrectly() {
        var savedHabit: Habit? = null
        val onSave: (Habit) -> Unit = { savedHabit = it }

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave)
            }
        }

        // Initially, no reminder checkbox should be visible
        composeRule.onNodeWithText("Set Reminder").assertDoesNotExist()

        // Click the "Set time" button
        composeRule.onNodeWithText("Set time (optional)").performClick()

        // Use UiDevice to interact with the TimePickerDialog
        val device = androidx.test.uiautomator.UiDevice.getInstance(
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        )

        // Find and click OK button
        val okButton = device.findObject(
            androidx.test.uiautomator.UiSelector().text("OK")
        )
        okButton.waitForExists(5000)
        okButton.click()

        // Wait for dialog to close and checkbox to appear
        composeRule.waitForIdle()

        // Now reminder checkbox should be visible
        composeRule.onNodeWithText("Set Reminder").assertIsDisplayed()

        // Check the reminder checkbox
        composeRule.onNode(
            isToggleable() and hasAnySibling(hasText("Set Reminder"))
        ).performClick()

        grantNotificationPermissionIfNeeded()

        // Enter habit name and save
        composeRule.onNodeWithText("Habit name").performTextInput("Test Habit")
        composeRule.onNodeWithText("Create habit").performClick()

        // Verify saved habit has time (whatever the picker set) and notifications enabled
        assertTrue(savedHabit?.time != null)
        assertTrue(savedHabit?.notificationsEnabled ?: false)
    }

    @Test
    fun givenNoTimeSet_WhenHabitCreated_ThenSavedWithoutTimeAndReminder() {
        var savedHabit: Habit? = null
        val onSave: (Habit) -> Unit = { savedHabit = it }

        composeRule.setContent {
            PreviewTheme {
                AddHabitForm(onSave)
            }
        }

        // Verify reminder checkbox is not visible
        composeRule.onNodeWithText("Set Reminder").assertDoesNotExist()

        // Enter habit name and save
        composeRule.onNodeWithText("Habit name").performTextInput("Simple Habit")
        composeRule.onNodeWithText("Create habit").performClick()

        // Verify saved habit
        assertEquals("Simple Habit", savedHabit?.name)
        assertEquals(null, savedHabit?.time)
        // notificationsEnabled might be false by default or irrelevant if time is null
        assertFalse(savedHabit?.notificationsEnabled ?: true)
    }
}
