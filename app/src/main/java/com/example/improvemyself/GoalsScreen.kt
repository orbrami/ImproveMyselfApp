package com.example.improvemyself

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen() {
    // State for holding the selected date in milliseconds
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    // State to control the visibility of the DatePickerDialog
    var showDatePicker by remember { mutableStateOf(false) }

    // State for the DatePicker itself
    val datePickerState = rememberDatePickerState()

    // Formatter for displaying the date
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showDatePicker = true }) {
            Text(text = "Set Your Goal Date")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedDateMillis != null) {
            val formattedDate = dateFormatter.format(Date(selectedDateMillis!!))
            Text(text = "Goal Date: $formattedDate")

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown Timer
            var timeRemaining by remember { mutableStateOf("") }

            // LaunchedEffect will recalculate the time every second
            LaunchedEffect(selectedDateMillis) {
                while (true) {
                    val currentTime = System.currentTimeMillis()
                    val diff = selectedDateMillis!! - currentTime

                    if (diff > 0) {
                        val days = diff / (1000 * 60 * 60 * 24)
                        val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                        val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                        val seconds = (diff % (1000 * 60)) / 1000
                        timeRemaining =
                            "Time left: $days days, $hours hours, $minutes minutes, $seconds seconds"
                    } else {
                        timeRemaining = "Goal Reached!"
                    }
                    delay(1.seconds)
                }
            }
            Text(text = timeRemaining)
        }
    }

    // DatePickerDialog logic
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}