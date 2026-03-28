package com.example.improvemyself.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(val name: String, val reps: String, val sets: String) : Parcelable

@Composable
fun WorkoutsScreen(exercises: SnapshotStateList<Exercise>) {
    var name by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Exercise") }, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = reps, onValueChange = { reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = sets, onValueChange = { sets = it }, label = { Text("Sets") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Button(onClick = { exercises.add(Exercise(name, reps, sets)); name = ""; reps = ""; sets = "" }, modifier = Modifier.align(Alignment.End).padding(top = 8.dp)) { Text("Add") }

        LazyColumn {
            items(exercises) { ex ->
                Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceBetween) {
                    Text("${ex.name}: ${ex.sets}x${ex.reps}")
                    IconButton(onClick = { exercises.remove(ex) }) { Icon(Icons.Default.Delete, null) }
                }
                HorizontalDivider()
            }
        }
    }
}
