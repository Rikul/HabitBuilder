package com.ofalvai.habittracker.feature.dashboard.di

import com.ofalvai.habittracker.core.common.HabitNotificationScheduler
import com.ofalvai.habittracker.feature.dashboard.notification.HabitNotificationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DashboardModule {
    @Binds
    abstract fun bindHabitNotificationScheduler(
        impl: HabitNotificationManager
    ): HabitNotificationScheduler
}
