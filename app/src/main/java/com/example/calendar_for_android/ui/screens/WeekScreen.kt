package com.example.calendar_for_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar_for_android.data.CalendarEvent
import com.example.calendar_for_android.utils.CalendarDataSource
import com.example.calendar_for_android.ui.components.EventItem
import com.example.calendar_for_android.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeekScreen(viewModel: CalendarViewModel, onEventClick: (Long) -> Unit) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val daysOfWeek = CalendarDataSource.getDaysOfWeek(selectedDate)
    val dateFormatter = DateTimeFormatter.ofPattern("EEE\nd")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setSelectedDate(selectedDate.minusWeeks(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
            }
            Text(
                text = "${daysOfWeek.first().month.name} ${daysOfWeek.first().year}",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { viewModel.setSelectedDate(selectedDate.plusWeeks(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
            }
        }

        // Days Header
        Row(modifier = Modifier.fillMaxWidth()) {
            SpacerColumn(modifier = Modifier.width(50.dp)) // Time column placeholder
            daysOfWeek.forEach { date ->
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setSelectedDate(date) }
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.format(dateFormatter),
                        textAlign = TextAlign.Center,
                        color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        HorizontalDivider()

        // Time Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                for (hour in 0..23) {
                    Row(
                        modifier = Modifier
                            .height(60.dp) // Each hour is 60dp height
                            .fillMaxWidth()
                    ) {
                        // Time label
                        Text(
                            text = String.format("%02d:00", hour),
                            modifier = Modifier
                                .width(50.dp)
                                .padding(end = 4.dp),
                            textAlign = TextAlign.End,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        // Grid lines
                        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
            
            // Events Overlay
            // This is a simplified approach. Complex overlapping events logic is omitted for brevity.
            Row(modifier = Modifier.fillMaxWidth().padding(start = 50.dp)) {
                 daysOfWeek.forEach { date ->
                     Box(modifier = Modifier.weight(1f).height(1440.dp)) { // 24 hours * 60dp = 1440dp
                         val daysEvents = events.filter {
                             val eventDate = Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                             eventDate == date
                         }
                         
                         daysEvents.forEach { event ->
                             val startDateTime = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault())
                             val endDateTime = Instant.ofEpochMilli(event.endTime).atZone(ZoneId.systemDefault())
                             
                             val startHour = startDateTime.hour
                             val startMinute = startDateTime.minute
                             val endHour = endDateTime.hour
                             val endMinute = endDateTime.minute
                             
                             // Calculate offset and height
                             // 1 hour = 60dp, 1 minute = 1dp
                             val topOffset = (startHour * 60 + startMinute).dp
                             val durationMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute)
                             val height = maxOf(durationMinutes, 30).dp // Minimum 30 min height for visibility

                             EventItem(
                                 event = event,
                                 modifier = Modifier
                                     .padding(1.dp)
                                     .fillMaxWidth()
                                     .offset(y = topOffset)
                                     .height(height),
                                 onClick = onEventClick
                             )
                         }
                     }
                 }
            }
        }
    }
}

@Composable
fun SpacerColumn(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {}
}
