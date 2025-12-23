package com.example.calendar_for_android

import android.app.Application
import com.example.calendar_for_android.data.AppDatabase
import com.example.calendar_for_android.data.CalendarRepository

import com.example.calendar_for_android.utils.ReminderManager

class CalendarApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { CalendarRepository(database.calendarEventDao()) }
    val reminderManager by lazy { ReminderManager(this) }
}

