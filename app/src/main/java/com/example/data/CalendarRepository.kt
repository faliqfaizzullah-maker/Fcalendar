package com.example.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class CalendarRepository(private val calendarDao: CalendarDao) {

    val allAccountsFlow: Flow<List<CalendarAccount>> = calendarDao.getAllAccountsFlow()
    val allEventsFlow: Flow<List<CalendarEvent>> = calendarDao.getAllEventsFlow()

    fun getEventsForAccountsFlow(accountIds: List<String>): Flow<List<CalendarEvent>> {
        return calendarDao.getEventsForAccountsFlow(accountIds)
    }

    suspend fun insertAccount(account: CalendarAccount) {
        calendarDao.insertAccount(account)
    }

    suspend fun deleteAccount(account: CalendarAccount) {
        // First delete events for this account to maintain integrity
        calendarDao.deleteEventsByAccountId(account.id)
        calendarDao.deleteAccount(account)
    }

    suspend fun toggleAccountActive(accountId: String, isActive: Boolean) {
        calendarDao.updateAccountActiveState(accountId, isActive)
    }

    suspend fun insertEvent(event: CalendarEvent): Long {
        return calendarDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEvent) {
        calendarDao.deleteEvent(event)
    }

    /**
     * Simulates downloading remote calendars and synchronizing events into the database.
     * Inserts standard customized dynamic events matching the current calendar year/month
     * so they are immediately visible on the UI screen when sync completes.
     */
    suspend fun syncAccount(accountId: String): Boolean {
        val account = calendarDao.getAccountById(accountId) ?: return false
        
        // Simulating network delay to show polished UI progress bar and status states
        delay(1800)

        // Clear existing synced events to avoid duplicating
        calendarDao.deleteEventsByAccountId(accountId)

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val newEvents = when (account.type) {
            "google" -> listOf(
                createSimulatedEvent(
                    accountId = accountId,
                    title = "Google Sync: Retro & Sprint Planning",
                    desc = "Reflect on accomplishments, outline upcoming milestones, and delegate tasks.",
                    loc = "Google Meet Online Room",
                    daysOffset = 0, // Today
                    startHour = 10,
                    startMin = 0,
                    durationMins = 60,
                    notifyBefore = 15,
                    style = "SOUND"
                ),
                createSimulatedEvent(
                    accountId = accountId,
                    title = "Google Sync: Design System Jam",
                    desc = "Collaborative review of new Jetpack Compose frosted glass custom components.",
                    loc = "Figma Board Room",
                    daysOffset = 1, // Tomorrow
                    startHour = 14,
                    startMin = 30,
                    durationMins = 90,
                    notifyBefore = 5,
                    style = "SOUND"
                ),
                createSimulatedEvent(
                    accountId = accountId,
                    title = "Google Sync: Sync Team Dinner",
                    desc = "Casual dinner after a highly productive sprint sync cycle.",
                    loc = "The Glass House Bistro",
                    daysOffset = 3,
                    startHour = 18,
                    startMin = 30,
                    durationMins = 120,
                    notifyBefore = 30,
                    style = "SOUND"
                )
            )
            "outlook" -> listOf(
                createSimulatedEvent(
                    accountId = accountId,
                    title = "Outlook Sync: Cross-functional QBR",
                    desc = "Quarterly business review aligning corporate directives and calendar progress.",
                    loc = "Microsoft Teams Live Screen",
                    daysOffset = 0, // Today
                    startHour = 15,
                    startMin = 0,
                    durationMins = 75,
                    notifyBefore = 15,
                    style = "SILENT"
                ),
                createSimulatedEvent(
                    accountId = accountId,
                    title = "Outlook Sync: Code Review & Security Audit",
                    desc = "Inspecting cryptographic hashes, database query sanitization, and room permissions.",
                    loc = "Office Conference Room 4B",
                    daysOffset = 2,
                    startHour = 11,
                    startMin = 15,
                    durationMins = 45,
                    notifyBefore = 10,
                    style = "SOUND"
                )
            )
            else -> listOf( // caldav / others
                createSimulatedEvent(
                    accountId = accountId,
                    title = "CalDAV Sync: Fitness Cardio Routine",
                    desc = "High-intensity interval training focusing on agility and recovery periods.",
                    loc = "Community Gym Center",
                    daysOffset = -1, // Yesterday
                    startHour = 8,
                    startMin = 0,
                    durationMins = 45,
                    notifyBefore = 0,
                    style = "SILENT"
                ),
                createSimulatedEvent(
                    accountId = accountId,
                    title = "CalDAV Sync: Book Reading & Cozy Hour",
                    desc = "Progressing through classic chapters on architectural design discipline and focus.",
                    loc = "Living Room Comfort Spot",
                    daysOffset = 2,
                    startHour = 20,
                    startMin = 0,
                    durationMins = 60,
                    notifyBefore = 15,
                    style = "SILENT"
                )
            )
        }

        // Save simulated synchronized events
        for (event in newEvents) {
            calendarDao.insertEvent(event)
        }

        // Update sync timestamp
        calendarDao.updateAccountSyncTime(accountId, System.currentTimeMillis())
        return true
    }

    private fun createSimulatedEvent(
        accountId: String,
        title: String,
        desc: String,
        loc: String,
        daysOffset: Int,
        startHour: Int,
        startMin: Int,
        durationMins: Int,
        notifyBefore: Int,
        style: String
    ): CalendarEvent {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysOffset)
        cal.set(Calendar.HOUR_OF_DAY, startHour)
        cal.set(Calendar.MINUTE, startMin)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        val startMillis = cal.timeInMillis
        val endMillis = startMillis + (durationMins * 60 * 1000)

        return CalendarEvent(
            accountId = accountId,
            title = title,
            description = desc,
            location = loc,
            startMillis = startMillis,
            endMillis = endMillis,
            notifyBeforeMins = notifyBefore,
            notifyStyle = style
        )
    }
}
