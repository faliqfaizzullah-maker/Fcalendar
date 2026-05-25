package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendar_accounts ORDER BY id ASC")
    fun getAllAccountsFlow(): Flow<List<CalendarAccount>>

    @Query("SELECT * FROM calendar_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): CalendarAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: CalendarAccount)

    @Delete
    suspend fun deleteAccount(account: CalendarAccount)

    @Query("UPDATE calendar_accounts SET isActive = :isActive WHERE id = :accountId")
    suspend fun updateAccountActiveState(accountId: String, isActive: Boolean)

    @Query("UPDATE calendar_accounts SET lastSynced = :timestamp WHERE id = :accountId")
    suspend fun updateAccountSyncTime(accountId: String, timestamp: Long)

    @Query("SELECT * FROM calendar_events ORDER BY startMillis ASC")
    fun getAllEventsFlow(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM calendar_events WHERE accountId = :accountId")
    suspend fun getEventsByAccount(accountId: String): List<CalendarEvent>

    @Query("SELECT * FROM calendar_events WHERE accountId IN (:accountIds) ORDER BY startMillis ASC")
    fun getEventsForAccountsFlow(accountIds: List<String>): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)

    @Query("DELETE FROM calendar_events WHERE accountId = :accountId")
    suspend fun deleteEventsByAccountId(accountId: String)
}
