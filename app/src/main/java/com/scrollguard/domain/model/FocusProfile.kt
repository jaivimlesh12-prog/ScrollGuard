package com.scrollguard.domain.model

/**
 * A user-defined focus profile that specifies which apps to block, when,
 * and optionally where.
 *
 * @property id             unique identifier.
 * @property name           user-facing profile name (e.g. "Study Mode").
 * @property blockedApps    list of package names to block.
 * @property startTime      start time as "HH:mm" (24-hour format).
 * @property endTime        end time as "HH:mm" (24-hour format).
 * @property daysOfWeek     days the profile is active (1 = Monday … 7 = Sunday).
 * @property isEnabled      whether the profile is currently active.
 * @property isLocationBased whether the profile uses a geofence trigger.
 * @property latitude       geofence centre latitude (if location-based).
 * @property longitude      geofence centre longitude (if location-based).
 * @property radiusMeters   geofence radius in metres (if location-based).
 */
data class FocusProfile(
    val id: Long,
    val name: String,
    val blockedApps: List<String>,
    val startTime: String,
    val endTime: String,
    val daysOfWeek: List<Int>,
    val isEnabled: Boolean,
    val isLocationBased: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Float? = null
)
