package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CalendarRepository(database.calendarDao())
    private val notificationHelper = NotificationHelper(application)

    // Current navigation view: "CALENDAR", "ACCOUNTS", "ADD_EVENT"
    private val _currentScreen = MutableStateFlow("CALENDAR")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Selected date state
    private val _selectedDate = MutableStateFlow<Calendar>(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    // Accounts synchronization state mapping (account ID to isSyncing)
    private val _syncingStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val syncingStates: StateFlow<Map<String, Boolean>> = _syncingStates.asStateFlow()

    // Accounts lists loaded from database
    val accounts: StateFlow<List<CalendarAccount>> = repository.allAccountsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Collect active accounts to filter events
    val activeAccountIds: StateFlow<List<String>> = accounts.map { accountList ->
        accountList.filter { it.isActive }.map { it.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Events matching active accounts
    val filteredEvents: StateFlow<List<CalendarEvent>> = activeAccountIds.flatMapLatest { ids ->
        if (ids.isEmpty()) flowOf(emptyList())
        else repository.getEventsForAccountsFlow(ids)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Pre-populate standard accounts if database starts empty, matching user info
        viewModelScope.launch {
            repository.allAccountsFlow.first().let { currentAccounts ->
                if (currentAccounts.isEmpty()) {
                    val defaultGoogle = CalendarAccount(
                        id = "google_personal",
                        name = "Google Calendar (Personal)",
                        email = "faliqfaizzullah@gmail.com",
                        type = "google",
                        colorHex = 0xFF4285F4,
                        isActive = true,
                        lastSynced = 0L
                    )
                    val defaultOutlook = CalendarAccount(
                        id = "outlook_work",
                        name = "Outlook (Work Team)",
                        email = "work.faliq@microsoft.com",
                        type = "outlook",
                        colorHex = 0xFF0078D4,
                        isActive = true,
                        lastSynced = 0L
                    )
                    val defaultCalDav = CalendarAccount(
                        id = "caldav_local",
                        name = "CalDAV Secure Storage",
                        email = "faliq@caldav.net",
                        type = "caldav",
                        colorHex = 0xFF4CAF50,
                        isActive = true,
                        lastSynced = 0L
                    )

                    repository.insertAccount(defaultGoogle)
                    repository.insertAccount(defaultOutlook)
                    repository.insertAccount(defaultCalDav)

                    // Auto-sync google by default to provide instant demonstration content
                    syncAccount(defaultGoogle.id)
                }
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectDate(calendar: Calendar) {
        _selectedDate.value = calendar
    }

    fun addMonths(amount: Int) {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedDate.value.timeInMillis
            add(Calendar.MONTH, amount)
        }
        _selectedDate.value = newCal
    }

    fun selectToday() {
        _selectedDate.value = Calendar.getInstance()
    }

    fun toggleAccount(accountId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAccountActive(accountId, isActive)
        }
    }

    fun deleteAccount(account: CalendarAccount) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    fun addCustomAccount(name: String, email: String, type: String, color: Long) {
        viewModelScope.launch {
            val shortId = "${type}_${System.currentTimeMillis() % 10000}"
            val newAcc = CalendarAccount(
                id = shortId,
                name = name,
                email = email,
                type = type,
                colorHex = color,
                isActive = true,
                lastSynced = 0L
            )
            repository.insertAccount(newAcc)
            syncAccount(newAcc.id)
        }
    }

    fun syncAccount(accountId: String) {
        viewModelScope.launch {
            _syncingStates.value = _syncingStates.value.toMutableMap().apply {
                put(accountId, true)
            }
            val success = repository.syncAccount(accountId)
            _syncingStates.value = _syncingStates.value.toMutableMap().apply {
                put(accountId, false)
            }
        }
    }

    fun syncAllAccounts() {
        viewModelScope.launch {
            val ids = accounts.value.map { it.id }
            for (id in ids) {
                launch {
                    syncAccount(id)
                }
            }
        }
    }

    fun addNewEvent(
        title: String,
        accountId: String,
        description: String,
        location: String,
        startCal: Calendar,
        endCal: Calendar,
        notifyMins: Int,
        notifyStyle: String,
        triggerNotificationNow: Boolean = true
    ) {
        viewModelScope.launch {
            val event = CalendarEvent(
                accountId = accountId,
                title = title.ifEmpty { "New Event" },
                description = description,
                location = location,
                startMillis = startCal.timeInMillis,
                endMillis = endCal.timeInMillis,
                notifyBeforeMins = notifyMins,
                notifyStyle = notifyStyle
            )
            val newId = repository.insertEvent(event)
            
            // Trigger actual custom local status-bar notification immediately for full functionality confirmation!
            if (triggerNotificationNow) {
                val insertedEventWithId = event.copy(id = newId)
                notificationHelper.showEventNotification(insertedEventWithId)
            }
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun triggerEventNotification(event: CalendarEvent) {
        notificationHelper.showEventNotification(event)
    }
}
