package com.scrollguard.di

import android.content.Context
import androidx.room.Room
import com.scrollguard.data.db.ScrollGuardDatabase
import com.scrollguard.data.db.dao.AddictionScoreDao
import com.scrollguard.data.db.dao.AppLimitDao
import com.scrollguard.data.db.dao.ChallengeDao
import com.scrollguard.data.db.dao.FocusScheduleDao
import com.scrollguard.data.db.dao.GameSessionDao
import com.scrollguard.data.db.dao.NotificationLogDao
import com.scrollguard.data.db.dao.PhonePickupDao
import com.scrollguard.data.db.dao.ReelCountDao
import com.scrollguard.data.db.dao.ScreenTimeDao
import com.scrollguard.data.db.dao.UsageScreenshotDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScrollGuardDatabase {
        return Room.databaseBuilder(
            context,
            ScrollGuardDatabase::class.java,
            "scrollguard.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideReelCountDao(db: ScrollGuardDatabase): ReelCountDao = db.reelCountDao()

    @Provides
    fun provideScreenTimeDao(db: ScrollGuardDatabase): ScreenTimeDao = db.screenTimeDao()

    @Provides
    fun provideAppLimitDao(db: ScrollGuardDatabase): AppLimitDao = db.appLimitDao()

    @Provides
    fun provideGameSessionDao(db: ScrollGuardDatabase): GameSessionDao = db.gameSessionDao()

    @Provides
    fun providePhonePickupDao(db: ScrollGuardDatabase): PhonePickupDao = db.phonePickupDao()

    @Provides
    fun provideNotificationLogDao(db: ScrollGuardDatabase): NotificationLogDao = db.notificationLogDao()

    @Provides
    fun provideChallengeDao(db: ScrollGuardDatabase): ChallengeDao = db.challengeDao()

    @Provides
    fun provideFocusScheduleDao(db: ScrollGuardDatabase): FocusScheduleDao = db.focusScheduleDao()

    @Provides
    fun provideUsageScreenshotDao(db: ScrollGuardDatabase): UsageScreenshotDao = db.usageScreenshotDao()

    @Provides
    fun provideAddictionScoreDao(db: ScrollGuardDatabase): AddictionScoreDao = db.addictionScoreDao()
}
