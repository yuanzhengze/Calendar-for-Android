package com.example.calendar_for_android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromRepeatMode(value: RepeatMode): String {
        return value.name
    }

    @TypeConverter
    fun toRepeatMode(value: String): RepeatMode {
        return RepeatMode.valueOf(value)
    }
}

@Database(entities = [CalendarEvent::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
