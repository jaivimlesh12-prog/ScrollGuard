package com.scrollguard.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
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
import com.scrollguard.data.db.entity.AddictionScoreEntity
import com.scrollguard.data.db.entity.AppLimitEntity
import com.scrollguard.data.db.entity.ChallengeEntity
import com.scrollguard.data.db.entity.FocusScheduleEntity
import com.scrollguard.data.db.entity.GameSessionEntity
import com.scrollguard.data.db.entity.NotificationLogEntity
import com.scrollguard.data.db.entity.PhonePickupEntity
import com.scrollguard.data.db.entity.ReelCountEntity
import com.scrollguard.data.db.entity.ScreenTimeEntity
import com.scrollguard.data.db.entity.UsageScreenshotEntity

/**
 * Central Room database for the ScrollGuard application.
 *
 * Contains all ten entity tables and exposes abstract DAO accessors.
 * The singleton instance is provided by Hilt via a `@Module` — no
 * companion-object builder is needed here.
 */
@Database(
    entities = [
        ReelCountEntity::class,
        ScreenTimeEntity::class,
        AppLimitEntity::class,
        GameSessionEntity::class,
        PhonePickupEntity::class,
        NotificationLogEntity::class,
        ChallengeEntity::class,
        FocusScheduleEntity::class,
        UsageScreenshotEntity::class,
        AddictionScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ScrollGuardDatabase : RoomDatabase() {

    /** DAO for reel/short-video count tracking. */
    abstract fun reelCountDao(): ReelCountDao

    /** DAO for per-app screen-time records. */
    abstract fun screenTimeDao(): ScreenTimeDao

    /** DAO for user-configured app usage limits. */
    abstract fun appLimitDao(): AppLimitDao

    /** DAO for gaming session records. */
    abstract fun gameSessionDao(): GameSessionDao

    /** DAO for phone pickup events. */
    abstract fun phonePickupDao(): PhonePickupDao

    /** DAO for notification count logs. */
    abstract fun notificationLogDao(): NotificationLogDao

    /** DAO for user challenges. */
    abstract fun challengeDao(): ChallengeDao

    /** DAO for focus-mode schedules. */
    abstract fun focusScheduleDao(): FocusScheduleDao

    /** DAO for usage screenshot metadata. */
    abstract fun usageScreenshotDao(): UsageScreenshotDao

    /** DAO for per-app addiction scores. */
    abstract fun addictionScoreDao(): AddictionScoreDao
}
