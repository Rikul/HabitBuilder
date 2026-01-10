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

import android.Manifest
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ofalvai.habittracker.core.testing.BaseInstrumentedTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NotificationPermissionTest : BaseInstrumentedTest() {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenAndroid13OrHigher_ThenPostNotificationsPermissionIsDeclaredInManifest() {
        if (android.os.Build.VERSION.SDK_INT < 33) {
            // Permission only exists on Android 13+, skip on lower versions
            return
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )

        val requestedPermissions = packageInfo.requestedPermissions?.toList() ?: emptyList()

        assertTrue(
            "POST_NOTIFICATIONS permission should be declared in AndroidManifest.xml",
            requestedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS)
        )
    }
}
