package com.scrollguard.domain.model

/**
 * Computed "addiction score" for a single application.
 *
 * The score is an integer 0–100 where higher values indicate more
 * concerning usage patterns. [factors] lists the individual signals
 * that contributed to the score (e.g. "Excessive screen time",
 * "Frequent late-night usage").
 *
 * @property packageName the Android package name.
 * @property appName     the user-visible application label.
 * @property score       computed addiction score (0–100).
 * @property factors     human-readable list of contributing factors.
 * @property trend       whether the score is improving, worsening, or stable.
 */
data class AddictionScore(
    val packageName: String,
    val appName: String,
    val score: Int,
    val factors: List<String>,
    val trend: ScoreTrend
)

/**
 * Direction of change for an [AddictionScore] over time.
 */
enum class ScoreTrend {
    IMPROVING,
    WORSENING,
    STABLE
}
