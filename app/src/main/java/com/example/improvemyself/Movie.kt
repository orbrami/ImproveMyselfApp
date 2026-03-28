package com.example.improvemyself.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class Movie(val name: String, val rating: Int, val timestamp: Long = System.currentTimeMillis()) : Parcelable

@Composable
fun MoviesScreen(
    movies: SnapshotStateList<Movie>,
    movieGoal: MutableIntState, // Added this parameter
    onSave: () -> Unit          // Added this parameter
) {
    var movieName by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    // Logic to count movies watched THIS week
    val moviesThisWeek = remember(movies.size) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        movies.count { it.timestamp >= calendar.timeInMillis }
    }

    val remaining = (movieGoal.intValue - moviesThisWeek).coerceAtLeast(0)

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // --- Weekly Goal Section ---
        Card(Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Weekly Goal Status", fontWeight = FontWeight.Bold)
                Text("$moviesThisWeek / ${movieGoal.intValue} Movies", style = MaterialTheme.typography.headlineMedium)

                if (remaining > 0) {
                    Text("Watch $remaining more movies to hit your goal!")
                } else if (movieGoal.intValue > 0) {
                    Text("Goal Reached! 🎉", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (movieGoal.intValue == 0) "" else movieGoal.intValue.toString(),
                    onValueChange = {
                        movieGoal.intValue = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                        onSave()
                    },
                    label = { Text("Set Weekly Goal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(150.dp)
                )
            }
        }

        // --- Add Movie Section ---
        OutlinedTextField(
            value = movieName,
            onValueChange = { movieName = it },
            label = { Text("Movie Name") },
            modifier = Modifier.fillMaxWidth()
        )

        RatingBar(currentRating = rating, onRatingChanged = { rating = it })

        Button(
            onClick = {
                movies.add(Movie(movieName, rating))
                movieName = ""; rating = 0
                onSave()
            },
            enabled = movieName.isNotBlank() && rating > 0,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Log Movie") }

        HorizontalDivider(Modifier.padding(vertical = 16.dp))

        // --- History List ---
        LazyColumn(Modifier.weight(1f)) {
            items(movies.reversed()) { movie ->
                Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text(movie.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Rating: ${movie.rating}/5", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { movies.remove(movie); onSave() }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun RatingBar(currentRating: Int, onRatingChanged: (Int) -> Unit) {
    Row {
        (1..5).forEach { index ->
            IconButton(onClick = { onRatingChanged(index) }) {
                Icon(
                    imageVector = if (index <= currentRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (index <= currentRating) Color(0xFFFFD700) else Color.Gray
                )
            }
        }
    }
}
