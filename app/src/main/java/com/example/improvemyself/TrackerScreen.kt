package com.example.improvemyself.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.improvemyself.DailyRecord

@Composable
fun TrackerScreen(
    habits: List<Habit>,
    onSave: (DailyRecord) -> Unit,
    onShowHistory: () -> Unit
) {
    val context = LocalContext.current
    var ateHealthy by remember { mutableStateOf(false) }
    var didWorkout by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val habitStates = remember { mutableStateMapOf<String, Boolean>() }

    // Use OpenMultipleDocuments to get persistent URI access
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        selectedImages = uris.take(4)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            item {
                TitledCheckbox("Ate Good Food", ateHealthy) { ateHealthy = it }
                TitledCheckbox("Completed Workout", didWorkout) { didWorkout = it }

                Spacer(Modifier.height(12.dp))

                Button(onClick = { launcher.launch(arrayOf("image/*")) }, Modifier.fillMaxWidth()) {
                    Text("Add 4 Progress Photos (${selectedImages.size}/4)")
                }

                LazyRow(Modifier.padding(vertical = 8.dp)) {
                    items(selectedImages) { uri ->
                        Card(Modifier.size(100.dp).padding(4.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
            }

            item { Text("Habits Tracker", style = MaterialTheme.typography.titleMedium) }

            items(habits) { habit ->
                TitledCheckbox(habit.name, habitStates[habit.name] ?: false) { habitStates[habit.name] = it }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            onClick = {
                onSave(DailyRecord(
                    date = System.currentTimeMillis(),
                    ateHealthy = ateHealthy,
                    didWorkout = didWorkout,
                    completedHabits = habitStates.filter { it.value }.keys.toList(),
                    photoUris = selectedImages.map { it.toString() }
                ))
            }
        ) {
            Text("Save Today's Progress")
        }

        OutlinedButton(
            onClick = onShowHistory,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("View History")
        }
    }
}

@Composable
fun TitledCheckbox(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(title, modifier = Modifier.weight(1f))
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
