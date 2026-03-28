package com.example.improvemyself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DietScreen(foodList: SnapshotStateList<String>) {
    var foodName by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Allowed Diet List", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("Add Food") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (foodName.isNotBlank()) {
                    foodList.add(foodName)
                    foodName = ""
                }
            }) { Text("Add") }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(foodList) { food ->
                Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceBetween) {
                    Text(food)
                    IconButton(onClick = { foodList.remove(food) }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
