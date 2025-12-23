package com.example.calendar_for_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calendar_for_android.ui.components.BottomNavigationBar
import com.example.calendar_for_android.ui.navigation.Screen
import com.example.calendar_for_android.ui.screens.DayScreen
import com.example.calendar_for_android.ui.screens.EventEditorScreen
import com.example.calendar_for_android.ui.screens.MonthScreen
import com.example.calendar_for_android.ui.screens.WeekScreen
import com.example.calendar_for_android.ui.screens.YearScreen
import com.example.calendar_for_android.ui.theme.Calendar_for_androidTheme
import com.example.calendar_for_android.viewmodel.CalendarViewModel
import com.example.calendar_for_android.viewmodel.CalendarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as CalendarApplication).repository
        val reminderManager = (application as CalendarApplication).reminderManager
        setContent {
            Calendar_for_androidTheme {
                CalendarApp(repository = repository, reminderManager = reminderManager)
            }
        }
    }
}

@Composable
fun CalendarApp(
    repository: com.example.calendar_for_android.data.CalendarRepository,
    reminderManager: com.example.calendar_for_android.utils.ReminderManager
) {
    val navController = rememberNavController()
    val viewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(repository, reminderManager)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEvent.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Month.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Year.route) {
                YearScreen(viewModel = viewModel, onMonthClick = {
                    navController.navigate(Screen.Month.route) {
                         popUpTo(Screen.Year.route) { saveState = true }
                         launchSingleTop = true
                         restoreState = true
                    }
                })
            }
            composable(Screen.Month.route) {
                MonthScreen(viewModel = viewModel, onEventClick = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                })
            }
            composable(Screen.Week.route) {
                WeekScreen(viewModel = viewModel, onEventClick = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                })
            }
            composable(Screen.Day.route) {
                DayScreen(viewModel = viewModel, onEventClick = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                })
            }
            composable(Screen.AddEvent.route) {
                EventEditorScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.EditEvent.route,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
                EventEditorScreen(viewModel = viewModel, eventId = eventId, onBack = { navController.popBackStack() })
            }
        }
    }
}
