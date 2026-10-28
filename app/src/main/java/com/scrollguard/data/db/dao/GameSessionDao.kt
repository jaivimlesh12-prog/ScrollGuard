package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrollguard.data.db.entity.GameSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [GameSessionEntity].
 *
 * Supports per-date and date-range queries, aggregate analytics such as
 * total game time, session counts, most-played game, and average session
 * length per game.
 */
@Dao
interface GameSessionDao {

    /** Inserts a game-session record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(entity: GameSessionEntity)

    /** Returns all game sessions for the given [date]. */
    @Query("SELECT * FROM game_sessions WHERE date = :date")
    fun getSessionsByDate(date: Long): Flow<List<GameSessionEntity>>

    /** Returns all game sessions within the inclusive date range. */
    @Query("SELECT * FROM game_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getSessionsByDateRange(startDate: Long, endDate: Long): Flow<List<GameSessionEntity>>

    /** Returns sessions for a specific game within the date range. */
    @Query(
        """
        SELECT * FROM game_sessions
        WHERE package_name = :packageName
          AND date BETWEEN :startDate AND :endDate
        ORDER BY start_time ASC
        """
    )
    fun getSessionsByGame(
        packageName: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<GameSessionEntity>>

    /** Returns total gaming duration in milliseconds for the given [date]. */
    @Query("SELECT SUM(duration_ms) FROM game_sessions WHERE date = :date")
    fun getTotalGameTimeForDate(date: Long): Flow<Long?>

    /** Returns the number of game sessions on the given [date]. */
    @Query("SELECT COUNT(*) FROM game_sessions WHERE date = :date")
    fun getGameSessionCountForDate(date: Long): Flow<Int?>

    /**
     * Returns the most-played game by total duration within the range.
     *
     * The result is a single representative [GameSessionEntity] row from the
     * group with the highest aggregate duration. Returns null when there are
     * no sessions in the range.
     */
    @Query(
        """
        SELECT * FROM game_sessions
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY package_name
        ORDER BY SUM(duration_ms) DESC
        LIMIT 1
        """
    )
    fun getMostPlayedGame(startDate: Long, endDate: Long): Flow<GameSessionEntity?>

    /** Returns the average session duration for a specific game in the range. */
    @Query(
        """
        SELECT AVG(duration_ms) FROM game_sessions
        WHERE package_name = :packageName
          AND date BETWEEN :startDate AND :endDate
        """
    )
    fun getAverageSessionLength(
        packageName: String,
        startDate: Long,
        endDate: Long
    ): Flow<Long?>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM game_sessions WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
