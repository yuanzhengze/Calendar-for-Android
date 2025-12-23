package com.example.calendar_for_android.ui.navigation

sealed class Screen(val route: String) {
    object Year : Screen("year")
    object Month : Screen("month")
    object Week : Screen("week")
    object Day : Screen("day")
    object AddEvent : Screen("add_event")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: Long) = "edit_event/$eventId"
    }
}
