package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_accounts")
data class CalendarAccount(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val type: String, // "google", "outlook", "caldav"
    val colorHex: Long, // Hex color for events of this account
    val isActive: Boolean = true, // Whether it is shown in calendar view
    val lastSynced: Long = 0L // Last synchronization timestamp
)
