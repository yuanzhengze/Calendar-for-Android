package com.example.calendar_for_android.data

import kotlinx.coroutines.flow.Flow

class CalendarRepository(private val calendarEventDao: CalendarEventDao) {
    
    fun getEventsInRange(start: Long, end: Long): Flow<List<CalendarEvent>> {
        return calendarEventDao.getEventsInRange(start, end)
    }

    fun getAllEvents(): Flow<List<CalendarEvent>> {
        return calendarEventDao.getAllEvents()
    }

    suspend fun getEventById(id: Long): CalendarEvent? {
        return calendarEventDao.getEventById(id)
    }

    suspend fun insertEvent(event: CalendarEvent): Long {
        return calendarEventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: CalendarEvent) {
        calendarEventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEvent) {
        calendarEventDao.deleteEvent(event)
    }
}
