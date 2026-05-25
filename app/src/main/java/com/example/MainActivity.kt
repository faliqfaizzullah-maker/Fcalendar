package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // We force darkTheme = true to deliver a premium cosmic dark mode frosted glass interface
            MyApplicationTheme(darkTheme = true) {
                
                // Request dynamic notifications permissions on Android 13+ (API 33+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { /* Handle response dynamically */ }
                    )
                    LaunchedEffect(Unit) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val selectedCal by viewModel.selectedDate.collectAsStateWithLifecycle()
                val accounts by viewModel.accounts.collectAsStateWithLifecycle()
                val filteredEvents by viewModel.filteredEvents.collectAsStateWithLifecycle()
                val syncingStates by viewModel.syncingStates.collectAsStateWithLifecycle()

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally { width -> if (targetState == "CALENDAR") -width else width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> if (targetState == "CALENDAR") width else -width } + fadeOut()
                    },
                    label = "GlassCalendarTransitions"
                ) { screen ->
                    when (screen) {
                        "CALENDAR" -> {
                            FrostedGlassBackground {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    containerColor = Color.Transparent, // transparent scaffold so we see frosted glass backing
                                    floatingActionButton = {
                                        FloatingActionButton(
                                            onClick = { viewModel.navigateTo("ADD_EVENT") },
                                            containerColor = Color(0xFFE2B6FF),
                                            contentColor = Color(0xFF0C0A1A),
                                            modifier = Modifier
                                                .testTag("add_event_fab")
                                                .padding(bottom = 16.dp, end = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Schedule Premium Event"
                                            )
                                        }
                                    }
                                ) { paddingValues ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(paddingValues)
                                            .padding(horizontal = 12.dp)
                                    ) {
                                        // Header
                                        CalendarTopHeader(
                                            viewModel = viewModel,
                                            selectedCal = selectedCal,
                                            onNavigateToAccounts = { viewModel.navigateTo("ACCOUNTS") },
                                            onNavigateToAddEvent = { viewModel.navigateTo("ADD_EVENT") }
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Horizontal sync streams toggles
                                        AccountsSwitchstrip(
                                            accounts = accounts,
                                            onToggle = { id, active -> viewModel.toggleAccount(id, active) }
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Frosted Monthly Grid View
                                        DaysGridView(
                                            viewModel = viewModel,
                                            selectedCal = selectedCal,
                                            events = filteredEvents,
                                            accounts = accounts
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Detailed Interactive Agenda view
                                        DayAgendaView(
                                            viewModel = viewModel,
                                            selectedCal = selectedCal,
                                            events = filteredEvents,
                                            accounts = accounts,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        "ACCOUNTS" -> {
                            AccountsManagerView(
                                viewModel = viewModel,
                                accounts = accounts,
                                syncingStates = syncingStates,
                                onBack = { viewModel.navigateTo("CALENDAR") }
                            )
                        }

                        "ADD_EVENT" -> {
                            AddEventView(
                                viewModel = viewModel,
                                accounts = accounts,
                                selectedCal = selectedCal,
                                onBack = { viewModel.navigateTo("CALENDAR") }
                            )
                        }
                    }
                }
            }
        }
    }
}
