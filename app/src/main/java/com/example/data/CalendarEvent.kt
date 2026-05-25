package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val accountId: String,
    val title: String,
    val description: String,
    val location: String,
    val startMillis: Long,
    val endMillis: Long,
    val isAllDay: Boolean = false,
    val notifyBeforeMins: Int = 15, // -1 means None, 0 means At Event Time, etc.
    val notifyStyle: String = "SOUND" // "SOUND", "SILENT"
)
