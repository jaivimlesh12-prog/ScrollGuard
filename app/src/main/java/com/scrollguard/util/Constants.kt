package com.scrollguard.util

/**
 * Application-wide constants for ScrollGuard.
 *
 * Contains well-known package names, notification channel IDs,
 * and measurement defaults used across the monitoring and analytics layers.
 */
object Constants {

    // ── Notification channel IDs ────────────────────────────────────────────
    const val CHANNEL_MONITORING = "scrollguard_monitoring"
    const val CHANNEL_ALERTS = "scrollguard_alerts"
    const val CHANNEL_DAILY = "scrollguard_daily"
    const val CHANNEL_BLOCKER = "scrollguard_blocker"
    const val CHANNEL_EYESTRAIN = "scrollguard_eyestrain"

    // ── Key app package names ───────────────────────────────────────────────
    const val INSTAGRAM_PACKAGE = "com.instagram.android"
    const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    const val TWITTER_PACKAGE = "com.twitter.android"
    const val FACEBOOK_PACKAGE = "com.facebook.katana"
    const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
    const val SNAPCHAT_PACKAGE = "com.snapchat.android"
    const val REDDIT_PACKAGE = "com.reddit.frontpage"

    // ── Short-form content URLs ─────────────────────────────────────────────
    const val INSTAGRAM_REELS_URL = "instagram.com/reels"
    const val YOUTUBE_SHORTS_URL = "youtube.com/shorts"

    // ── Browser package names ───────────────────────────────────────────────
    val COMMON_BROWSERS = listOf(
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.brave.browser",
        "com.opera.browser",
        "com.microsoft.emmx"
    )

    // ── Social media package names ──────────────────────────────────────────
    val SOCIAL_MEDIA_PACKAGES = listOf(
        INSTAGRAM_PACKAGE,
        YOUTUBE_PACKAGE,
        TWITTER_PACKAGE,
        FACEBOOK_PACKAGE,
        TIKTOK_PACKAGE,
        SNAPCHAT_PACKAGE,
        REDDIT_PACKAGE
    )

    // ── Measurement defaults ────────────────────────────────────────────────
    /** Approximate vertical pixels traveled per reel/shorts swipe. */
    const val SCROLL_DISTANCE_PER_REEL_PX = 2400

    /** Default screen DPI used when the actual value is unavailable. */
    const val SCREEN_DPI_DEFAULT = 440f
}
