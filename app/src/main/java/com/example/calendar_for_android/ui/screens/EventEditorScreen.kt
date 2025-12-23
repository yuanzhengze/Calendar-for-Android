package com.example.calendar_for_android.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.calendar_for_android.data.CalendarEvent
import com.example.calendar_for_android.data.RepeatMode
import com.example.calendar_for_android.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(
    viewModel: CalendarViewModel, 
    eventId: Long? = null, 
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isAllDay by remember { mutableStateOf(false) }

    val selectedDate by viewModel.selectedDate.collectAsState()
    var startDate by remember { mutableStateOf(selectedDate) }
    var startTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var endDate by remember { mutableStateOf(selectedDate) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1).withSecond(0).withNano(0)) }

    var repeatMode by remember { mutableStateOf(RepeatMode.NONE) }
    var reminderMinutes by remember { mutableStateOf<Int?>(null) }
    
    var expandedRepeat by remember { mutableStateOf(false) }
    var expandedReminder by remember { mutableStateOf(false) }
    
    // Load event if editing
    LaunchedEffect(eventId) {
        if (eventId != null && eventId != 0L) {
            val event = viewModel.getEventById(eventId)
            if (event != null) {
                title = event.title
                description = event.description
                location = event.location
                isAllDay = event.isAllDay
                repeatMode = event.repeatMode
                reminderMinutes = event.reminderMinutesBefore
                
                val startZone = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.systemDefault())
                startDate = startZone.toLocalDate()
                startTime = startZone.toLocalTime()
                
                val endZone = Instant.ofEpochMilli(event.endTime).atZone(ZoneId.systemDefault())
                endDate = endZone.toLocalDate()
                endTime = endZone.toLocalTime()
            }
        }
    }

    // Date Pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Time Pickers
    val context = LocalContext.current
    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> startTime = LocalTime.of(hour, minute) },
        startTime.hour,
        startTime.minute,
        true
    )
    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> endTime = LocalTime.of(hour, minute) },
        endTime.hour,
        endTime.minute,
        true
    )

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventId != null && eventId != 0L) "Edit Event" else "Add Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (eventId != null && eventId != 0L) {
                        IconButton(onClick = {
                            // Delete event
                             viewModel.deleteEvent(CalendarEvent(id = eventId, title="", startTime=0, endTime=0))
                             onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("All Day")
                Switch(checked = isAllDay, onCheckedChange = { isAllDay = it })
            }

            // Start Date/Time
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    label = { Text("Start Date") },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartDatePicker = true },
                    enabled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (!isAllDay) {
                    OutlinedTextField(
                        value = startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = {},
                        label = { Text("Start Time") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { startTimePickerDialog.show() },
                        enabled = false
                    )
                }
            }

            // End Date/Time
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    label = { Text("End Date") },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndDatePicker = true },
                    enabled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (!isAllDay) {
                    OutlinedTextField(
                        value = endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = {},
                        label = { Text("End Time") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { endTimePickerDialog.show() },
                        enabled = false
                    )
                }
            }

            // Repeat Mode
            ExposedDropdownMenuBox(
                expanded = expandedRepeat,
                onExpandedChange = { expandedRepeat = !expandedRepeat }
            ) {
                OutlinedTextField(
                    value = repeatMode.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Repeat") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRepeat,
                    onDismissRequest = { expandedRepeat = false }
                ) {
                    RepeatMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.name) },
                            onClick = {
                                repeatMode = mode
                                expandedRepeat = false
                            }
                        )
                    }
                }
            }

            // Reminders
            ExposedDropdownMenuBox(
                expanded = expandedReminder,
                onExpandedChange = { expandedReminder = !expandedReminder }
            ) {
                val reminderText = when (reminderMinutes) {
                    null -> "No Reminder"
                    0 -> "At time of event"
                    10 -> "10 minutes before"
                    30 -> "30 minutes before"
                    60 -> "1 hour before"
                    1440 -> "1 day before"
                    else -> "$reminderMinutes minutes before"
                }
                
                OutlinedTextField(
                    value = reminderText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reminder") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReminder) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedReminder,
                    onDismissRequest = { expandedReminder = false }
                ) {
                    val options = listOf(
                        null to "No Reminder",
                        0 to "At time of event",
                        10 to "10 minutes before",
                        30 to "30 minutes before",
                        60 to "1 hour before",
                        1440 to "1 day before"
                    )
                    
                    options.forEach { (minutes, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                reminderMinutes = minutes
                                expandedReminder = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val startDateTime = if (isAllDay) startDate.atStartOfDay() else startDate.atTime(startTime)
                    val endDateTime = if (isAllDay) endDate.atStartOfDay() else endDate.atTime(endTime)
                    
                    val startEpoch = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                    val endEpoch = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

                    val event = CalendarEvent(
                        id = eventId ?: 0,
                        title = title,
                        description = description,
                        location = location,
                        startTime = startEpoch,
                        endTime = endEpoch,
                        isAllDay = isAllDay,
                        repeatMode = repeatMode,
                        reminderMinutesBefore = reminderMinutes
                    )
                    
                    if (eventId != null && eventId != 0L) {
                        viewModel.updateEvent(event)
                    } else {
                        viewModel.addEvent(event)
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (eventId != null && eventId != 0L) "Update Event" else "Save Event")
            }
            
            if (eventId != null && eventId != 0L) {
                Button(
                    onClick = {
                        // Dummy event with ID is enough for delete if dao uses primary key
                        viewModel.deleteEvent(CalendarEvent(id = eventId, title="", startTime=0, endTime=0))
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Event")
                }
            }
        }
    }
}
