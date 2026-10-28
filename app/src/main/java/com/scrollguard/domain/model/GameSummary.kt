package com.scrollguard.domain.model

/**
 * Summary of a gaming session for a single game.
 *
 * @property packageName      the Android package name of the game.
 * @property gameName         the user-visible game label.
 * @property category         the game sub-category (Action, Puzzle, etc.).
 * @property totalSessions    number of play sessions.
 * @property totalTimeMs      cumulative play time in milliseconds.
 * @property avgSessionMs     average session length in milliseconds.
 * @property longestSessionMs longest single session in milliseconds.
 */
data class GameSummary(
    val packageName: String,
    val gameName: String,
    val category: String,
    val totalSessions: Int,
    val totalTimeMs: Long,
    val avgSessionMs: Long,
    val longestSessionMs: Long
)
