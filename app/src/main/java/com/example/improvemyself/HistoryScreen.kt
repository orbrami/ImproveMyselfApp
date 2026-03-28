package com.example.improvemyself.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.improvemyself.WeeklyRecord // FIX: THE MISSING IMPORT
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    history: List<WeeklyRecord>,
    onDeleteRecord: (String) -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // --- FULL SCREEN IMAGE VIEWER ---
    if (selectedImageUri != null) {
        Dialog(
            onDismissRequest = { selectedImageUri = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Full Screen",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    // Close Button
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }
                }
            }
        }
    }

    if (history.isEmpty() || history.all { it.dailyRecords.isEmpty() }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No history saved yet!")
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            // Get all daily records across all weeks, sorted by newest first
            val allDailyRecords = history.flatMap { it.dailyRecords }.sortedByDescending { it.date }

            items(allDailyRecords) { day ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sdf.format(Date(day.date)),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onDeleteRecord(day.id) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Text("Healthy: ${if(day.ateHealthy) "✅" else "❌"} | Workout: ${if(day.didWorkout) "✅" else "❌"}")

                        if (day.completedHabits.isNotEmpty()) {
                            Text(
                                text = "Habits: ${day.completedHabits.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // --- CLICKABLE THUMBNAILS ---
                        if (day.photoUris.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(day.photoUris) { uri ->
                                    Card(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clickable { selectedImageUri = uri } // CLICK TO SEE BIG
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
