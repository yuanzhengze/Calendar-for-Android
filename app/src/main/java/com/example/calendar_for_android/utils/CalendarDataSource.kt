package com.example.calendar_for_android.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

object CalendarDataSource {
    fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 (Mon) - 7 (Sun)
        
        // Let's assume Monday start. 
        // 1 (Mon) -> 0 padding
        // 7 (Sun) -> 6 padding
        val paddingDays = firstDayOfWeek - 1
        
        val startDay = firstDayOfMonth.minusDays(paddingDays.toLong())
        
        val days = mutableListOf<LocalDate>()
        // 6 rows * 7 columns = 42 days
        for (i in 0 until 42) {
            days.add(startDay.plusDays(i.toLong()))
        }
        return days
    }

    fun getDaysOfWeek(date: LocalDate): List<LocalDate> {
        val firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = mutableListOf<LocalDate>()
        for (i in 0 until 7) {
            days.add(firstDayOfWeek.plusDays(i.toLong()))
        }
        return days
    }
}
