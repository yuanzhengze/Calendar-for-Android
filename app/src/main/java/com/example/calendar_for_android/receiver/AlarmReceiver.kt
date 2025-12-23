package com.example.calendar_for_android.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.calendar_for_android.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra("event_id", -1)
        val title = intent.getStringExtra("event_title") ?: "Event Reminder"
        
        showNotification(context, eventId, title)
    }

    private fun showNotification(context: Context, eventId: Long, title: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "calendar_reminders"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Event Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        
        // Ensure we use a valid icon. Using mipmap/ic_launcher for safety if drawable not found, 
        // but setSmallIcon requires a drawable resource id. 
        // Default projects have ic_launcher_foreground.
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("Event Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify(eventId.toInt(), notification)
    }
}
