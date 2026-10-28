package com.scrollguard.domain.model

/**
 * Represents a wellness challenge the user can participate in.
 *
 * @property id            unique identifier.
 * @property title         short display title (e.g. "No Reels Day").
 * @property description   full description of the challenge.
 * @property type          temporal scope of the challenge.
 * @property targetType    what is being measured (e.g. "screen_time", "reels", "pickups").
 * @property targetValue   the goal value the user aims to meet.
 * @property currentValue  the user's current progress value.
 * @property progress      normalised progress [0f .. 1f].
 * @property startDate     start epoch-millis.
 * @property endDate       end epoch-millis.
 * @property isCompleted   whether the challenge was successfully completed.
 * @property isActive      whether the challenge is currently running.
 */
data class Challenge(
    val id: Long,
    val title: String,
    val description: String,
    val type: ChallengeType,
    val targetType: String,
    val targetValue: Float,
    val currentValue: Float,
    val progress: Float,
    val startDate: Long,
    val endDate: Long,
    val isCompleted: Boolean,
    val isActive: Boolean
)

/**
 * Temporal scope for a [Challenge].
 */
enum class ChallengeType {
    DAILY,
    WEEKLY,
    MONTHLY
}
