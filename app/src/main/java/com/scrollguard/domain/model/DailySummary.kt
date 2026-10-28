package com.scrollguard.domain.model

/**
 * Aggregated daily wellness summary.
 *
 * @property date                start-of-day epoch-millis.
 * @property totalScreenTimeMs   total foreground screen time in milliseconds.
 * @property totalReels          total short-form content views.
 * @property totalPickups        number of times the device was unlocked/picked up.
 * @property totalGameTimeMs     total game foreground time in milliseconds.
 * @property totalNotifications  total notifications received.
 * @property appsBlocked         number of distinct apps that were blocked.
 * @property streakDays          consecutive days the user met their goals.
 */
data class DailySummary(
    val date: Long,
    val totalScreenTimeMs: Long,
    val totalReels: Int,
    val totalPickups: Int,
    val totalGameTimeMs: Long,
    val totalNotifications: Int,
    val appsBlocked: Int,
    val streakDays: Int
)
