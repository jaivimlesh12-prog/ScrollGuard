package com.scrollguard.domain.model

/**
 * Summary of short-form content (reels / shorts) consumption for a given day.
 *
 * @property totalCount      total reels viewed across all sources.
 * @property instagramCount  reels viewed on Instagram.
 * @property youtubeCount    shorts viewed on YouTube.
 * @property browserCount    reels/shorts viewed through a mobile browser.
 * @property limit           the user-configured daily reel limit.
 * @property date            the start-of-day epoch-millis for this summary.
 */
data class ReelSummary(
    val totalCount: Int,
    val instagramCount: Int,
    val youtubeCount: Int,
    val browserCount: Int,
    val limit: Int,
    val date: Long
)
