package com.example.improvemyself.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habit(val name: String, var isCompleted: Boolean = false) : Parcelable

@Composable
fun HabitsScreen(habits: SnapshotStateList<Habit>) {
    var habitName by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            OutlinedTextField(value = habitName, onValueChange = { habitName = it }, label = { Text("New Habit") }, modifier = Modifier.weight(1f))
            Button(onClick = { if(habitName.isNotBlank()) { habits.add(Habit(habitName)); habitName = "" } }, Modifier.padding(start = 8.dp)) { Text("Add") }
        }
        LazyColumn {
            items(habits) { habit ->
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(habit.name)
                    IconButton(onClick = { habits.remove(habit) }) { Icon(Icons.Default.Delete, null) }
                }
                HorizontalDivider()
            }
        }
    }
}
