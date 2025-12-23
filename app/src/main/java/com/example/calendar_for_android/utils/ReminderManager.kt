package com.example.calendar_for_android.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.calendar_for_android.data.CalendarEvent
import com.example.calendar_for_android.receiver.AlarmReceiver

class ReminderManager(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setReminder(event: CalendarEvent) {
        if (event.reminderMinutesBefore == null) return
        
        val reminderTime = event.startTime - (event.reminderMinutesBefore * 60 * 1000)
        if (reminderTime < System.currentTimeMillis()) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("event_id", event.id)
            putExtra("event_title", event.title)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for reliable alarms
        // Note: Needs SCHEDULE_EXACT_ALARM permission on Android 12+
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Handle missing permission
            e.printStackTrace()
        }
    }
    
    fun cancelReminder(event: CalendarEvent) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
