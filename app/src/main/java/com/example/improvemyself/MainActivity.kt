package com.example.improvemyself

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.improvemyself.ui.screens.*
import com.example.improvemyself.ui.theme.ImproveMyselfTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.util.*

// --- DATA MODELS ---
@Parcelize
data class DailyRecord(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val ateHealthy: Boolean = false,
    val didWorkout: Boolean = false,
    val completedHabits: List<String> = emptyList(),
    val photoUris: List<String> = emptyList()
) : Parcelable

@Parcelize
data class WeeklyRecord(
    val startDate: Long,
    val endDate: Long,
    val dailyRecords: List<DailyRecord> = emptyList()
) : Parcelable

data class NavItem(val label: String, val icon: ImageVector, val route: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ImproveMyselfTheme { ImproveMyselfApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImproveMyselfApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val gson = remember { Gson() }
    val prefs = remember { context.getSharedPreferences("brami_final_v4", Context.MODE_PRIVATE) }

    // --- PERSISTENCE LOADERS ---
    fun <T> load(key: String, typeToken: TypeToken<T>): T? =
        gson.fromJson(prefs.getString(key, null), typeToken.type)

    val weekly = remember { mutableStateListOf<WeeklyRecord>().apply { addAll(load("w", object : TypeToken<List<WeeklyRecord>>() {}) ?: emptyList()) } }
    val habits = remember { mutableStateListOf<Habit>().apply { addAll(load("h", object : TypeToken<List<Habit>>() {}) ?: listOf(Habit("Drink Water"))) } }
    val movies = remember { mutableStateListOf<Movie>().apply { addAll(load("m", object : TypeToken<List<Movie>>() {}) ?: emptyList()) } }
    val diet = remember { mutableStateListOf<String>().apply { addAll(load("d", object : TypeToken<List<String>>() {}) ?: emptyList()) } }
    val exercises = remember { mutableStateListOf<Exercise>().apply { addAll(load("e", object : TypeToken<List<Exercise>>() {}) ?: emptyList()) } }
    val movieGoal = remember { mutableIntStateOf(prefs.getInt("mg", 0)) }
    val goalDate = remember { mutableLongStateOf(prefs.getLong("gd", 0L)) }

    fun save() {
        prefs.edit().apply {
            putString("w", gson.toJson(weekly.toList()))
            putString("h", gson.toJson(habits.toList()))
            putString("m", gson.toJson(movies.toList()))
            putString("d", gson.toJson(diet.toList()))
            putString("e", gson.toJson(exercises.toList()))
            putInt("mg", movieGoal.intValue)
            putLong("gd", goalDate.longValue)
        }.apply()
    }

    var selIdx by rememberSaveable { mutableIntStateOf(3) } // Default to Tracker

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Made By Or Brami", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, shadowElevation = 12.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navItems = listOf(
                        NavItem("Goal", Icons.Default.Flag, "goal"),
                        NavItem("Workouts", Icons.Default.FitnessCenter, "workouts"),
                        NavItem("Habits", Icons.Default.Checklist, "habits"),
                        NavItem("Tracker", Icons.Default.TaskAlt, "tracker"),
                        NavItem("Diet", Icons.Default.Fastfood, "diet"),
                        NavItem("Movies", Icons.Default.Movie, "movies"),
                        NavItem("History", Icons.Default.History, "history")
                    )

                    navItems.forEachIndexed { i, item ->
                        val isSelected = selIdx == i
                        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                        Column(
                            modifier = Modifier
                                .width(90.dp)
                                .clickable {
                                    selIdx = i
                                    navController.navigate(item.route)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(item.icon, item.label, tint = color, modifier = Modifier.size(28.dp))
                            Text(item.label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    ) { p ->
        NavHost(navController, "tracker", Modifier.padding(p)) {
            composable("goal") { GoalTabScreen(goalDate, onDateSet = { goalDate.longValue = it; save() }) }
            composable("workouts") { WorkoutsScreen(exercises); SideEffect { save() } }
            composable("habits") { HabitsScreen(habits); SideEffect { save() } }
            composable("diet") { DietScreen(diet); SideEffect { save() } }
            composable("movies") { MoviesScreen(movies, movieGoal, onSave = { save() }) }
            composable("tracker") {
                TrackerScreen(habits, onShowHistory = { navController.navigate("history") }, onSave = { r ->
                    weekly.add(WeeklyRecord(r.date, r.date, listOf(r)))
                    save()
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                })
            }
            composable("history") {
                HistoryScreen(history = weekly, onDeleteRecord = { id ->
                    weekly.forEachIndexed { index, week ->
                        val updated = week.dailyRecords.toMutableList()
                        if (updated.removeIf { it.id == id }) weekly[index] = week.copy(dailyRecords = updated)
                    }
                    weekly.removeIf { it.dailyRecords.isEmpty() }
                    save()
                })
            }
        }
    }
}
