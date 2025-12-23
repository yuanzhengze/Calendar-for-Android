package com.example.calendar_for_android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar_for_android.utils.CalendarDataSource
import com.example.calendar_for_android.viewmodel.CalendarViewModel
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun YearScreen(viewModel: CalendarViewModel, onMonthClick: () -> Unit) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentYear = selectedDate.year

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setSelectedDate(selectedDate.minusYears(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
            }
            Text(
                text = "$currentYear",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { viewModel.setSelectedDate(selectedDate.plusYears(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
            }
        }

        // Months Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Month.values()) { month ->
                MonthMiniView(
                    year = currentYear,
                    month = month,
                    onClick = {
                        viewModel.setSelectedDate(selectedDate.withMonth(month.value).withDayOfMonth(1))
                        onMonthClick()
                    }
                )
            }
        }
    }
}

@Composable
fun MonthMiniView(year: Int, month: Month, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        val days = CalendarDataSource.getDaysOfMonth(YearMonth.of(year, month))
        
        Column {
             days.chunked(7).forEach { week ->
                 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                     week.forEach { date ->
                         Text(
                             text = if (date.month == month) date.dayOfMonth.toString() else "",
                             fontSize = 8.sp,
                             textAlign = TextAlign.Center,
                             modifier = Modifier.weight(1f),
                             color = if (date.month == month) MaterialTheme.colorScheme.onSurface else Color.Transparent
                         )
                     }
                 }
             }
        }
    }
}
