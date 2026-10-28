package com.scrollguard.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Date and time utility functions for ScrollGuard.
 *
 * Provides helpers for computing day/week/month boundaries, formatting
 * durations and timestamps, and generating sequences of day-boundary
 * timestamps.
 */
object DateUtils {

    /**
     * Returns the epoch-millis value at 00:00:00.000 of the day containing
     * the given [timestamp] (defaults to *now*).
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Returns the epoch-millis value at 00:00:00.000 on Monday of the week
     * containing the given [timestamp].
     */
    fun getStartOfWeek(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Returns the epoch-millis value at 00:00:00.000 on the 1st day of the
     * month containing the given [timestamp].
     */
    fun getStartOfMonth(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Formats a duration in milliseconds into a human-readable string.
     *
     * Examples: `"2h 15m"`, `"45m"`, `"30s"`, `"0s"`.
     */
    fun formatDuration(ms: Long): String {
        if (ms < 0) return "0s"
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m")
            if (hours == 0L && minutes == 0L) append("${seconds}s")
        }.trim()
    }

    /**
     * Formats a timestamp as a date string, e.g. `"Oct 28, 2026"`.
     */
    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    /**
     * Formats a timestamp as a time string, e.g. `"7:45 PM"`.
     */
    fun formatTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    /**
     * Returns a list of start-of-day timestamps for every day in the
     * inclusive range from [start] to [end].
     */
    fun getDaysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = getStartOfDay(start)
        }
        val endDay = getStartOfDay(end)

        while (calendar.timeInMillis <= endDay) {
            days.add(calendar.timeInMillis)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }
}
