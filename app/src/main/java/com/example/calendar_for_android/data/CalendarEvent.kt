package com.example.calendar_for_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RepeatMode {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean = false,
    val location: String = "",
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val reminderMinutesBefore: Int? = null // null means no reminder
)
