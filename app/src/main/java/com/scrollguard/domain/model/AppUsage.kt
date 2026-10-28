package com.scrollguard.domain.model

import android.graphics.drawable.Drawable

/**
 * Represents aggregated usage data for a single application.
 *
 * @property packageName  the Android package name (e.g. `com.instagram.android`).
 * @property appName      the user-visible application label.
 * @property category     the resolved category (Social Media, Games, etc.).
 * @property usageMs      total foreground time in milliseconds.
 * @property launchCount  the number of times the app was opened.
 * @property reelCount    the number of short-form content views (reels / shorts).
 * @property icon         the app launcher icon, or `null` if unavailable.
 */
data class AppUsage(
    val packageName: String,
    val appName: String,
    val category: String,
    val usageMs: Long,
    val launchCount: Int,
    val reelCount: Int,
    val icon: Drawable? = null
)
