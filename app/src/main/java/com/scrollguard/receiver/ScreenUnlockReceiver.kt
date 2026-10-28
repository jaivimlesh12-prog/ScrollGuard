package com.scrollguard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scrollguard.data.repository.ScreenTimeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver listening for device unlock events (USER_PRESENT)
 * to count phone pickups throughout the day.
 */
class ScreenUnlockReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ScreenUnlockEntryPoint {
        fun screenTimeRepository(): ScreenTimeRepository
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_USER_PRESENT) {
            val pendingResult = goAsync()
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                ScreenUnlockEntryPoint::class.java
            )
            val repository = hiltEntryPoint.screenTimeRepository()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repository.recordPickup()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
