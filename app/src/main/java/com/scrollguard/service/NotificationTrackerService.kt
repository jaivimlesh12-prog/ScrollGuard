package com.scrollguard.service

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.scrollguard.data.db.dao.NotificationLogDao
import com.scrollguard.data.db.entity.NotificationLogEntity
import com.scrollguard.util.DateUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Notification listener service that counts notifications per app
 * to identify the noisiest apps and calculate distraction metrics.
 */
class NotificationTrackerService : NotificationListenerService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationTrackerEntryPoint {
        fun notificationLogDao(): NotificationLogDao
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val notificationLogDao: NotificationLogDao by lazy {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            NotificationTrackerEntryPoint::class.java
        )
        hiltEntryPoint.notificationLogDao()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val packageName = sbn.packageName ?: return

        // Skip our own notifications
        if (packageName == this.packageName) return

        serviceScope.launch {
            try {
                val pm = packageManager
                val appName = try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    packageName
                }

                val today = DateUtils.getStartOfDay()
                val entity = NotificationLogEntity(
                    packageName = packageName,
                    appName = appName,
                    count = 1,
                    date = today
                )
                notificationLogDao.upsertLog(entity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
