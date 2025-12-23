package com.example.calendar_for_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar_for_android.utils.CalendarDataSource
import com.example.calendar_for_android.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun MonthScreen(viewModel: CalendarViewModel, onEventClick: (Long) -> Unit) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val yearMonth = YearMonth.from(selectedDate)
    val days = CalendarDataSource.getDaysOfMonth(yearMonth)
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val json = reader.readText()
                        viewModel.importEventsJson(json)
                        Toast.makeText(context, "Events Imported", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Import Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setSelectedDate(selectedDate.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                IconButton(onClick = { viewModel.setSelectedDate(selectedDate.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export Events (JSON)") },
                            onClick = {
                                showMenu = false
                                scope.launch {
                                    val json = viewModel.exportEventsJson()
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, json)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Export Events")
                                    context.startActivity(shareIntent)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Import Events (JSON)") },
                            onClick = {
                                showMenu = false
                                importLauncher.launch("application/json") // or text/plain or */*
                            }
                        )
                    }
                }
            }
        }

        // Days of week
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxSize()) {
            items(days) { date ->
                val isSelected = date == selectedDate
                val isCurrentMonth = date.month == yearMonth.month
                val hasEvent = events.any { 
                    val eventDate = java.time.Instant.ofEpochMilli(it.startTime).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    eventDate == date
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewModel.setSelectedDate(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                    else if (isCurrentMonth) MaterialTheme.colorScheme.onSurface 
                                    else Color.Gray
                        )
                        if (hasEvent) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .height(4.dp)
                                    .fillMaxWidth(0.5f)
                                    .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, shape = CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
