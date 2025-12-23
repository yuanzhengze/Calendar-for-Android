package com.example.calendar_for_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendar_for_android.data.CalendarEvent
import com.example.calendar_for_android.data.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

import com.example.calendar_for_android.utils.ReminderManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarViewModel(
    private val repository: CalendarRepository,
    private val reminderManager: ReminderManager
) : ViewModel() {

    private val gson = Gson()
    
    // ... existing code ...

    suspend fun exportEventsJson(): String {
        return withContext(Dispatchers.IO) {
            val events = repository.getAllEvents().stateIn(viewModelScope).value
            gson.toJson(events)
        }
    }

    suspend fun importEventsJson(json: String) {
        withContext(Dispatchers.IO) {
            try {
                val type = object : TypeToken<List<CalendarEvent>>() {}.type
                val events: List<CalendarEvent> = gson.fromJson(json, type)
                events.forEach { event ->
                    // Reset ID to 0 to treat as new event
                    val newEvent = event.copy(id = 0)
                    val id = repository.insertEvent(newEvent)
                    val eventWithId = newEvent.copy(id = id)
                    if (event.reminderMinutesBefore != null) {
                        reminderManager.setReminder(eventWithId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Fetch all events and filter in UI/Business logic for simplicity with recurring events
    val allEvents = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            val id = repository.insertEvent(event)
            val eventWithId = event.copy(id = id)
            if (event.reminderMinutesBefore != null) {
                reminderManager.setReminder(eventWithId)
            }
        }
    }

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.updateEvent(event)
            // Cancel old reminder just in case and set new one
            reminderManager.cancelReminder(event)
            if (event.reminderMinutesBefore != null) {
                reminderManager.setReminder(event)
            }
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            reminderManager.cancelReminder(event)
        }
    }

    suspend fun getEventById(id: Long): CalendarEvent? {
        return repository.getEventById(id)
    }
}

class CalendarViewModelFactory(
    private val repository: CalendarRepository,
    private val reminderManager: ReminderManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository, reminderManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
