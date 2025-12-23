package com.example.calendar_for_android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events WHERE startTime >= :start AND startTime <= :end ORDER BY startTime ASC")
    fun getEventsInRange(start: Long, end: Long): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM calendar_events WHERE id = :id")
    suspend fun getEventById(id: Long): CalendarEvent?

    @Query("SELECT * FROM calendar_events")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent): Long

    @Update
    suspend fun updateEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)
}
