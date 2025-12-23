package com.example.calendar_for_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendar_for_android.data.CalendarEvent

@Composable
fun EventItem(event: CalendarEvent, modifier: Modifier = Modifier, onClick: (Long) -> Unit) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .clickable { onClick(event.id) }
            .padding(2.dp)
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1
        )
    }
}
