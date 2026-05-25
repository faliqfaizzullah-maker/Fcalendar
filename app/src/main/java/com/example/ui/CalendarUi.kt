package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarAccount
import com.example.data.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

// Helper model to represent a single calendar grid cell
data class CalendarDay(
    val day: Int,
    val month: Int,
    val year: Int,
    val isCurrentMonth: Boolean,
    val dateCalendar: Calendar
)

// List of available account colors in color picker
val ACCOUNT_PALETTE_COLORS = listOf(
    0xFF4285F4, // Google Blue
    0xFF0078D4, // Outlook Blue
    0xFFE81123, // Warm Red
    0xFF107C41, // Outlook Green
    0xFFF2C811, // Mustard Gold
    0xFF8E24AA, // Vivid Purple
    0xFF00ACC1, // Vibrant Teal
    0xFFE67E22  // Tangerine Orange
)

@Composable
fun FrostedGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D12)) // Deep graphite background from Professional Polish
    ) {
        // Aesthetic glowing dynamic radial background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Upper Right glow: Professional Indigo (indigo-600/20)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF4F46E5).copy(alpha = 0.20f), Color.Transparent),
                    center = Offset(size.width * 0.95f, size.height * 0.05f),
                    radius = size.width * 0.75f
                )
            )
            // Bottom Left glow: Fuchsia Glow (fuchsia-600/10)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD946EF).copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.05f, size.height * 0.95f),
                    radius = size.width * 0.85f
                )
            )
        }
        
        // Render content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
        ) {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(28.dp), // Styled soft corner curve from design
    borderAlpha: Float = 0.10f, // White/10 border alpha from design template
    bgAlpha: Float = 0.05f, // White/5 translucent glass container from design template
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = bgAlpha),
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = borderAlpha)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    tag: String = ""
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .testTag(tag)
            .size(40.dp) // Perfect touch targets with compact elegant housing
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f)) // bg-white/5 backdrop from design
            .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape) // border-white/10 from design
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun CalendarTopHeader(
    viewModel: CalendarViewModel,
    selectedCal: Calendar,
    onNavigateToAccounts: () -> Unit,
    onNavigateToAddEvent: () -> Unit
) {
    val monthYearTextUpper = remember(selectedCal) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        sdf.format(selectedCal.time).uppercase()
    }
    val isSelectedToday = remember(selectedCal) {
        isSameDay(selectedCal, Calendar.getInstance())
    }
    val leadingHeadingText = if (isSelectedToday) {
        "Today"
    } else {
        remember(selectedCal) {
            SimpleDateFormat("EEEE d", Locale.getDefault()).format(selectedCal.time)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = monthYearTextUpper,
                color = Color(0xFF818CF8), // text-indigo-400
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = leadingHeadingText,
                color = Color.White,
                fontSize = 32.sp, // text-3xl font-bold tracking-tight
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Action Toolbar
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Select Today Button
            GlassIconButton(
                onClick = { viewModel.selectToday() },
                icon = Icons.Default.Today,
                contentDescription = "Jump to Today",
                tag = "toolbar_today_btn"
            )

            // Trigger global Sync button
            GlassIconButton(
                onClick = { viewModel.syncAllAccounts() },
                icon = Icons.Default.Sync,
                contentDescription = "Synchronize Accounts",
                tag = "toolbar_sync_btn",
                tint = Color(0xFFA5B4FC) // Premium slate-indigo link tint
            )

            // Stream connections / accounts manager button inside spectacular fuchsia-indigo gradient card
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFFD946EF))
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { onNavigateToAccounts() }
                    .testTag("toolbar_accounts_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Manage Accounts",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DaysGridView(
    viewModel: CalendarViewModel,
    selectedCal: Calendar,
    events: List<CalendarEvent>,
    accounts: List<CalendarAccount>
) {
    val days = remember(selectedCal) {
        getDaysForMonthGrid(selectedCal)
    }

    // Days representation (S M T W T F S)
    val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        padding = 12.dp
    ) {
        // Monthly Quick Picker Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendar Grid",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = (-0.3).sp,
                fontFamily = FontFamily.SansSerif
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { viewModel.addMonths(-1) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { viewModel.addMonths(1) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Weekday Labels Grid Column (slate-500 from design)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekdays.forEach { dayText ->
                Text(
                    text = dayText,
                    color = Color(0xFF64748B), // Slate-500 from design
                    fontSize = 11.sp, // text-[10px] uppercase style
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Calendar Day Cells Grid
        val chunkedDays = days.chunked(7)
        chunkedDays.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { calendarDay ->
                    val isSelected = isSameDay(calendarDay.dateCalendar, selectedCal)
                    val isToday = isSameDay(calendarDay.dateCalendar, Calendar.getInstance())

                    // Find events on this day
                    val dayEvents = remember(events, calendarDay) {
                        events.filter { event ->
                            isSameDay(event.startMillis, calendarDay.dateCalendar)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isSelected -> Color(0xFF6366F1).copy(alpha = 0.20f) // bg-indigo-500/20 active highlight
                                    isToday -> Color.White.copy(alpha = 0.05f) // Subtle overlay
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when {
                                    isSelected -> Color(0xFF6366F1).copy(alpha = 0.30f) // border-indigo-500/30 active border
                                    isToday -> Color(0xFF818CF8).copy(alpha = 0.40f) // Elegant Today indicator border outline
                                    else -> Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.selectDate(calendarDay.dateCalendar)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = calendarDay.day.toString(),
                                color = when {
                                    !calendarDay.isCurrentMonth -> Color.White.copy(alpha = 0.22f) // Out of month
                                    isSelected -> Color.White
                                    isToday -> Color(0xFF818CF8) // text-indigo-400 for Today
                                    else -> Color(0xFFE2E8F0) // slate-200 standard text
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )

                            // Little dynamic color indicator dots or active dot under selected/today
                            if (dayEvents.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val seenAccountColors = dayEvents
                                        .mapNotNull { e -> accounts.find { a -> a.id == e.accountId }?.colorHex }
                                        .distinct()
                                        .take(3)

                                    seenAccountColors.forEach { hexColor ->
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 1.dp)
                                                .size(5.dp)
                                                .background(Color(hexColor), CircleShape)
                                        )
                                    }
                                }
                            } else if (isSelected || isToday) {
                                // Design dot under selected/today if no events
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color(0xFF818CF8), CircleShape)
                                )
                            } else {
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountsSwitchstrip(
    accounts: List<CalendarAccount>,
    onToggle: (String, Boolean) -> Unit
) {
    if (accounts.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account Sync",
                color = Color(0xFF64748B), // text-slate-500 from design
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.SansSerif
            )
            // Beautiful sync lights with varying opacities as seen in Account Sync design
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF22C55E), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF22C55E).copy(alpha = 0.5f), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF22C55E).copy(alpha = 0.2f), CircleShape))
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(accounts) { account ->
                val cardBg = if (account.isActive) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f)
                val cardBorder = if (account.isActive) Color(account.colorHex).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .clickable { onToggle(account.id, !account.isActive) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(account.colorHex), CircleShape)
                    )
                    Text(
                        text = account.name,
                        color = if (account.isActive) Color.White else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (account.isActive) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Active",
                            tint = Color(account.colorHex),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayAgendaView(
    viewModel: CalendarViewModel,
    selectedCal: Calendar,
    events: List<CalendarEvent>,
    accounts: List<CalendarAccount>,
    modifier: Modifier = Modifier
) {
    // Collect agenda events matching selected date
    val dayEvents = remember(events, selectedCal) {
        events.filter { isSameDay(it.startMillis, selectedCal) }
    }

    val selectedDateText = remember(selectedCal) {
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        sdf.format(selectedCal.time)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Agenda",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
                fontFamily = FontFamily.SansSerif
            )
            // Beautiful count badge
            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${dayEvents.size} events",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (dayEvents.isEmpty()) {
            // Uncluttered, friendly Empty State
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                bgAlpha = 0.05f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = "No Events",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No events scheduled",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap the floating action icon to schedule an alert.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dayEvents, key = { it.id }) { event ->
                    val assocAccount = accounts.find { it.id == event.accountId }
                    val accountColor = assocAccount?.colorHex ?: 0xFF6366F1

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp)) // Soft curves of design template
                            .background(Color.White.copy(alpha = 0.08f)) // bg-white/10 equivalent backing
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(32.dp)) // border-white/15 equivalent border
                            .padding(20.dp)
                            .animateItem()
                    ) {
                        // Card Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fuchsia badge (next up or custom category badge style)
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFD946EF).copy(alpha = 0.15f), RoundedCornerShape(50)) // bg-fuchsia-500/20 style
                                    .border(1.dp, Color(0xFFD946EF).copy(alpha = 0.25f), RoundedCornerShape(50)) // border-fuchsia-500/30
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(Color(accountColor), CircleShape))
                                Text(
                                    text = (assocAccount?.name ?: "Personal").uppercase(),
                                    color = Color(0xFFF472B6), // text-fuchsia-300
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Event Action Controls (Alarm + Delete)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Instant notification trigger demo bubble (Outlook/iCloud styles)
                                IconButton(
                                    onClick = { viewModel.triggerEventNotification(event) },
                                    modifier = Modifier
                                        .testTag("trigger_notif_${event.id}")
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Trigger Reminder Now",
                                        tint = Color(0xFFFBBF24), // Vivid gold alarm tint
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                // Delete Button
                                IconButton(
                                    onClick = { viewModel.deleteEvent(event) },
                                    modifier = Modifier
                                        .testTag("delete_event_${event.id}")
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.20f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Event",
                                        tint = Color(0xFFFCA5A5),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        // Event Title
                        Text(
                            text = event.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp, // text-2xl/xl styling
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = (-0.3).sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Description sentence underneath
                        if (event.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.description,
                                color = Color(0xFF94A3B8), // slate-400 subtitle
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Footer Details Row with Time and Location
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Duration info (slate-300 / 11sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Duration info",
                                    tint = Color(0xFFCBD5E1), // slate-300
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = formatEventTimeRange(event.startMillis, event.endMillis),
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Location label
                            if (event.location.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = "Location Place icon",
                                        tint = Color(0xFF818CF8), // indigo-400
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = event.location,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Premium bottom labels: reminder details
                        if (event.notifyBeforeMins >= 0) {
                            Row(
                                modifier = Modifier.padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "⚡ Reminder: ${formatMinutesText(event.notifyBeforeMins)}",
                                        color = Color(0xFFA5B4FC),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountsManagerView(
    viewModel: CalendarViewModel,
    accounts: List<CalendarAccount>,
    syncingStates: Map<String, Boolean>,
    onBack: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Navigation Back Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassIconButton(
                        onClick = onBack,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Calendar",
                        tag = "back_from_accounts_btn"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Accounts & Sync",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                if (!showAddForm) {
                    Button(
                        onClick = { showAddForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("open_link_account_form")
                    ) {
                        Icon(imageVector = Icons.Default.Link, contentDescription = "Add Icon", modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Link Account", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            AnimatedVisibility(
                visible = showAddForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LinkAccountForm(
                    onDismiss = { showAddForm = false },
                    onSave = { name, email, type, color ->
                        viewModel.addCustomAccount(name, email, type, color)
                        showAddForm = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "My Unified Streams",
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            if (accounts.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No unified streams yet.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(accounts, key = { it.id }) { account ->
                        val isSyncing = syncingStates[account.id] ?: false
                        val lastSyncText = remember(account.lastSynced) {
                            if (account.lastSynced == 0L) {
                                "Never synchronized"
                            } else {
                                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                "Synced: " + sdf.format(Date(account.lastSynced))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Account Type Rounded Emblem
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(account.colorHex).copy(alpha = 0.25f))
                                    .border(1.dp, Color(account.colorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getAccountIcon(account.type),
                                    contentDescription = "Type",
                                    tint = Color(account.colorHex),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = account.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = account.email,
                                    color = Color.White.copy(alpha = 0.60f),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = lastSyncText,
                                    color = if (isSyncing) Color(0xFFC5E1A5) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Manual Sync Trigger
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color(account.colorHex),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        onClick = { viewModel.syncAccount(account.id) },
                                        modifier = Modifier
                                            .testTag("sync_specific_${account.id}")
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.10f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = "Sync Now",
                                            tint = Color.White.copy(alpha = 0.9f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Delete option
                                IconButton(
                                    onClick = { viewModel.deleteAccount(account) },
                                    modifier = Modifier
                                        .testTag("delete_account_${account.id}")
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red.copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Disconnect Account",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkAccountForm(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf("google") } // "google", "outlook", "caldav"
    var selectedColor by remember { mutableStateOf(ACCOUNT_PALETTE_COLORS[0]) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderAlpha = 0.35f,
        bgAlpha = 0.16f
    ) {
        Text(
            text = "Link Calendar Stream",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Account Display Name (e.g., Jam Projects)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_name_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color(0xFF818CF8), // Styled Professional Indigo-400
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                focusedBorderColor = Color(0xFF818CF8),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Access Email Address") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_email_field"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color(0xFF818CF8), // Styled Professional Indigo-400
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                focusedBorderColor = Color(0xFF818CF8),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Service Integration",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val types = listOf(
                "google" to "Google Cal",
                "outlook" to "Outlook Stream",
                "caldav" to "CalDAV Server"
            )
            types.forEach { (type, label) ->
                val active = accountType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                        .border(1.dp, if (active) Color(0xFF818CF8) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .clickable { accountType = type }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (active) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Choose Stream Color Theme",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ACCOUNT_PALETTE_COLORS) { colorValue ->
                val isSelected = selectedColor == colorValue
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(colorValue))
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = colorValue }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp), // Modern capsule corner from design
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Color.White)
            }

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onSave(name, email, accountType, selectedColor)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)), // Professional Indigo base
                shape = RoundedCornerShape(16.dp), // Modern capsule corner from design
                border = BorderStroke(1.dp, Color(0xFF818CF8)), // Accent border glow
                modifier = Modifier
                    .weight(1f)
                    .testTag("save_linked_account"),
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Link & Sync", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AddEventView(
    viewModel: CalendarViewModel,
    accounts: List<CalendarAccount>,
    selectedCal: Calendar,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedAccount by remember(accounts) {
        mutableStateOf(accounts.firstOrNull()?.id ?: "")
    }

    // Time definitions
    var startHour by remember { mutableStateOf(9) }
    var startMin by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(10) }
    var endMin by remember { mutableStateOf(0) }

    // Custom alerts definitions
    var notifyMinsBefore by remember { mutableStateOf(15) } // 15 min by default
    var notifyStyle by remember { mutableStateOf("SOUND") } // SOUND / SILENT

    FrostedGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back controller
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tag = "back_from_addevent_btn"
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Insert Custom Event",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (accounts.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "You must link at least one account in Accounts Settings tab to proceed.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            } else {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 16.dp
                ) {
                    // Subject field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("event_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF818CF8), // Styled Professional Indigo-400
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Associated Stream Picker
                    Text(
                        text = "Parent Calendar Stream",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    accounts.forEach { acc ->
                        val activeSelection = selectedAccount == acc.id
                        val activeColor = Color(acc.colorHex)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeSelection) Color.White.copy(alpha = 0.18f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (activeSelection) activeColor else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedAccount = acc.id }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(activeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = acc.name,
                                color = if (activeSelection) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                fontWeight = if (activeSelection) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date overview badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Date", tint = Color(0xFF818CF8), modifier = Modifier.size(16.dp))
                        Text(
                            text = "Date: " + SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(selectedCal.time),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Duration Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Start Hour", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DropdownTimeSelector(
                                    label = "Hour",
                                    current = startHour,
                                    range = 0..23,
                                    onSelect = { startHour = it },
                                    tag = "start_hour"
                                )
                                Text(":", color = Color.White)
                                DropdownTimeSelector(
                                    label = "Min",
                                    current = startMin,
                                    range = listOf(0, 15, 30, 45),
                                    onSelect = { startMin = it },
                                    tag = "start_min"
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "End Hour", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DropdownTimeSelector(
                                    label = "Hour",
                                    current = endHour,
                                    range = 0..23,
                                    onSelect = { endHour = it },
                                    tag = "end_hour"
                                )
                                Text(":", color = Color.White)
                                DropdownTimeSelector(
                                    label = "Min",
                                    current = endMin,
                                    range = listOf(0, 15, 30, 45),
                                    onSelect = { endMin = it },
                                    tag = "end_min"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Location Input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Event Location") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("event_location_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF818CF8), // Styled Professional Indigo-400
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description Input
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Event Description Details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("event_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF818CF8), // Styled Professional Indigo-400
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // CUSTOMIZABLE NOTIFICATIONS
                    Text(
                        text = "Customize Alert Delivery",
                        color = Color(0xFFFFA726),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Timing Chips Row
                    Text(
                        text = "Trigger Interval Timing",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    val offsets = listOf(
                        0 to "At Event",
                        5 to "5m prior",
                        15 to "15m prior",
                        60 to "1h prior",
                        1440 to "1d prior"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        offsets.forEach { (minsValue, titleText) ->
                            val activeVal = notifyMinsBefore == minsValue
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (activeVal) Color(0xFFFFA726).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, if (activeVal) Color(0xFFFFA726) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .clickable { notifyMinsBefore = minsValue }
                                    .padding(vertical = 6.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = titleText,
                                    color = if (activeVal) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Style choice: Sound vs Silent
                    Text(
                        text = "Alert Display Style",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val activeSound = notifyStyle == "SOUND"
                        val activeSilent = notifyStyle == "SILENT"

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeSound) Color(0xFFC5E1A5).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (activeSound) Color(0xFFC5E1A5) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .clickable { notifyStyle = "SOUND" }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFC5E1A5), modifier = Modifier.size(14.dp))
                                Text("Loud Sound & Popups", color = if (activeSound) Color.White else Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeSilent) Color.White.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (activeSilent) Color.White.copy(alpha = 0.61f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .clickable { notifyStyle = "SILENT" }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.VolumeMute, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                Text("Silent Icon Badges", color = if (activeSilent) Color.White else Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp), // Styled modern capsule from design
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard", color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val startCal = Calendar.getInstance().apply {
                                        timeInMillis = selectedCal.timeInMillis
                                        set(Calendar.HOUR_OF_DAY, startHour)
                                        set(Calendar.MINUTE, startMin)
                                        set(Calendar.SECOND, 0)
                                    }
                                    val endCal = Calendar.getInstance().apply {
                                        timeInMillis = selectedCal.timeInMillis
                                        set(Calendar.HOUR_OF_DAY, endHour)
                                        set(Calendar.MINUTE, endMin)
                                        set(Calendar.SECOND, 0)
                                    }

                                    // Let's add it
                                    viewModel.addNewEvent(
                                        title = title,
                                        accountId = selectedAccount,
                                        description = desc,
                                        location = location,
                                        startCal = startCal,
                                        endCal = endCal,
                                        notifyMins = notifyMinsBefore,
                                        notifyStyle = notifyStyle
                                    )
                                    onBack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)), // Tailwind Indigo-500 from design
                            shape = RoundedCornerShape(16.dp), // Styled modern capsule from design
                            border = BorderStroke(1.dp, Color(0xFF818CF8)), // Accent border glow (indigo-400)
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_event_submit_btn"),
                            enabled = title.isNotBlank() && selectedAccount.isNotEmpty()
                        ) {
                            Text("Create & Alert", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownTimeSelector(
    label: String,
    current: Int,
    range: List<Int>,
    onSelect: (Int) -> Unit,
    tag: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .testTag(tag)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
            .clickable { expanded = true }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        val displayStr = remember(current) {
            String.format("%02d", current)
        }
        Text(text = displayStr, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF16132C))
        ) {
            range.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = String.format("%02d", value),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Inline overload for normal range
@Composable
fun DropdownTimeSelector(
    label: String,
    current: Int,
    range: IntRange,
    onSelect: (Int) -> Unit,
    tag: String
) {
    DropdownTimeSelector(
        label = label,
        current = current,
        range = range.toList(),
        onSelect = onSelect,
        tag = tag
    )
}

// Core drawing helper function to resolve days of a month
fun getDaysForMonthGrid(selectedCal: Calendar): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()

    val cal = Calendar.getInstance().apply {
        timeInMillis = selectedCal.timeInMillis
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)

    // Compute first weekday starting day offset (1=Sunday, 2=Monday, ...)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val prevMonthOffset = firstDayOfWeek - 1

    // Fill previous month days
    val prevCal = (cal.clone() as Calendar).apply {
        add(Calendar.MONTH, -1)
    }
    val prevMaxDays = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in (prevMaxDays - prevMonthOffset + 1)..prevMaxDays) {
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, prevCal.get(Calendar.YEAR))
            set(Calendar.MONTH, prevCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, i)
        }
        days.add(
            CalendarDay(
                day = i,
                month = prevCal.get(Calendar.MONTH),
                year = prevCal.get(Calendar.YEAR),
                isCurrentMonth = false,
                dateCalendar = targetCal
            )
        )
    }

    // Fill current month days
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in 1..maxDays) {
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, i)
        }
        days.add(
            CalendarDay(
                day = i,
                month = currentMonth,
                year = currentYear,
                isCurrentMonth = true,
                dateCalendar = targetCal
            )
        )
    }

    // Fill remaining days of the next month for a complete calendar block grid
    val remainingDaysNeeded = 42 - days.size // standard 6-row layout is 42
    val nextCal = (cal.clone() as Calendar).apply {
        add(Calendar.MONTH, 1)
    }
    for (i in 1..remainingDaysNeeded) {
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, nextCal.get(Calendar.YEAR))
            set(Calendar.MONTH, nextCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, i)
        }
        days.add(
            CalendarDay(
                day = i,
                month = nextCal.get(Calendar.MONTH),
                year = nextCal.get(Calendar.YEAR),
                isCurrentMonth = false,
                dateCalendar = targetCal
            )
        )
    }

    return days
}

// Compares if two calendar dates represent the equivalent day, month, and year
fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

fun isSameDay(millis: Long, cal: Calendar): Boolean {
    val tempCal = Calendar.getInstance().apply { timeInMillis = millis }
    return isSameDay(tempCal, cal)
}

// Formats a starting and ending millisecond timestamp into a beautiful timeline slice text
fun formatEventTimeRange(startMillis: Long, endMillis: Long): String {
    val start = Date(startMillis)
    val end = Date(endMillis)
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return "${sdf.format(start)} - ${sdf.format(end)}"
}

fun formatMinutesText(mins: Int): String {
    return when (mins) {
        -1 -> "None"
        0 -> "At Event"
        5 -> "5m"
        15 -> "15m"
        30 -> "30m"
        60 -> "1h"
        1440 -> "1d"
        else -> "${mins}m"
    }
}

// Maps short string types Google/Outlook/CalDAV to Vector Icons
fun getAccountIcon(type: String): ImageVector {
    return when (type) {
        "google" -> Icons.Default.Mail
        "outlook" -> Icons.Default.Business
        else -> Icons.Default.CloudQueue
    }
}
