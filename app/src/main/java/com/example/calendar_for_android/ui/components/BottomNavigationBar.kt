package com.example.calendar_for_android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.calendar_for_android.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Month,
        Screen.Week,
        Screen.Day
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Year -> Icon(Icons.Default.DateRange, contentDescription = "Year")
                        Screen.Month -> Icon(Icons.Default.CalendarMonth, contentDescription = "Month")
                        Screen.Week -> Icon(Icons.Default.CalendarViewWeek, contentDescription = "Week")
                        Screen.Day -> Icon(Icons.Default.CalendarViewDay, contentDescription = "Day")
                        else -> {}
                    }
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Month -> "Month"
                            Screen.Week -> "Week"
                            Screen.Day -> "Day"
                            else -> ""
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
