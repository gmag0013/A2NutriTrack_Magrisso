package com.fit2081.a1_grace_35721383.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.ui.home.HomeScreen1
import com.fit2081.a1_grace_35721383.ui.home.NutriCoach
import com.fit2081.a1_grace_35721383.ui.settings.SettingsActivity
import com.fit2081.a1_grace_35721383.ui.home.NavItem
import com.fit2081.a1_grace_35721383.ui.home.BottomNavigationBar
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
/*
I used Chat GPT (https://chatgpt.com/) to a assist with progress bars, layout, and renaming of categories.
[Draft copies for this file: 4]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */

class InsightsBreakdown : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1_Grace_35721383Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White
                    ) {
                        InsightsScreen()
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen() {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val loggedInUserId = sharedPref.getInt("userId", -1)
    val db = AppDatabase.getInstance(context)
    val patientDao = db.patientDao()

    var gender by remember { mutableStateOf<String?>(null) }
    var patient by remember { mutableStateOf<Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (loggedInUserId != -1) {
            patient = withContext(Dispatchers.IO) {
                patientDao.getPatientOnce(loggedInUserId)
            }
            gender = patient?.sex?.lowercase()
            isLoading = false
        } else {
            Log.e("InsightsBreakdown", "No logged in user found in session.")
            isLoading = false
        }
    }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Insights", Icons.Default.Info),
        NavItem("NutriCoach", Icons.Default.Face),
        NavItem("Settings", Icons.Default.Settings)
    )

    var selectedIndex by remember { mutableStateOf(1) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navItems, selectedIndex) { index ->
                selectedIndex = index
                when (index) {
                    0 -> context.startActivity(Intent(context, HomeScreen1::class.java))
                    2 -> context.startActivity(Intent(context, NutriCoach::class.java))
                    3 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Insights: Food Score",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            val foodComponents = listOf(
                "Vegetables", "Fruit", "Grainsandcereals", "Wholegrains",
                "Meatandalternatives", "Dairyandalternatives",
                "Water", "UnsaturatedFat", "Sodium",
                "Sugar", "Alcohol", "Discretionary"
            )

            foodComponents.forEach { component ->
                val score = when (gender) {
                    "male" -> getMaleScore(patient, component)
                    "female" -> getFemaleScore(patient, component)
                    else -> 0f
                }

                val progress = (score / 10f).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatLabel(component),
                        fontSize = 10.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(3f)
                            .height(6.dp),
                        color = Color(0xFF6200EE)
                    )

                    Text(
                        text = "Score: ${score}/10",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val totalScore = when (gender) {
                "male" -> patient?.HEIFAtotalscoreMale ?: 0f
                "female" -> patient?.HEIFAtotalscoreFemale ?: 0f
                else -> 0f
            }

            val totalProgress = (totalScore / 100f).coerceIn(0f, 1f)

            Text(
                "Total Food Quality Score",
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = totalProgress,
                    modifier = Modifier
                        .weight(3f)
                        .height(6.dp),
                    color = Color(0xFF6200EE)
                )

                Text(
                    text = "Total Score: ${totalScore}/100",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, NutriCoach::class.java))
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Improve my diet", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out my NutriTrack food quality score! This app helps you eat better 🎯🍎")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share via")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Share with friends", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

fun formatLabel(rawLabel: String): String {
    return when (rawLabel) {
        "Grainsandcereals" -> "Grains & Cereals"
        "Wholegrains" -> "Whole Grains"
        "Meatandalternatives" -> "Meats & Alternatives"
        "Dairyandalternatives" -> "Dairy"
        else -> rawLabel.replace(Regex("([a-z])([A-Z])"), "$1 $2")
    }
}

fun getMaleScore(patient: Patient?, component: String): Float {
    return when (component) {
        "Vegetables" -> patient?.VegetablesHEIFAscoreMale
        "Fruit" -> patient?.FruitHEIFAscoreMale
        "Grainsandcereals" -> patient?.GrainsandcerealsHEIFAscoreMale
        "Wholegrains" -> patient?.WholegrainsHEIFAscoreMale
        "Meatandalternatives" -> patient?.MeatandalternativesHEIFAscoreMale
        "Dairyandalternatives" -> patient?.DairyandalternativesHEIFAscoreMale
        "Water" -> patient?.WaterHEIFAscoreMale
        "UnsaturatedFat" -> patient?.UnsaturatedFatHEIFAscoreMale
        "Sodium" -> patient?.SodiumHEIFAscoreMale
        "Sugar" -> patient?.SugarHEIFAscoreMale
        "Alcohol" -> patient?.AlcoholHEIFAscoreMale
        "Discretionary" -> patient?.DiscretionaryHEIFAscoreMale
        else -> 0f
    } ?: 0f
}

fun getFemaleScore(patient: Patient?, component: String): Float {
    return when (component) {
        "Vegetables" -> patient?.VegetablesHEIFAscoreFemale
        "Fruit" -> patient?.FruitHEIFAscoreFemale
        "Grainsandcereals" -> patient?.GrainsandcerealsHEIFAscoreFemale
        "Wholegrains" -> patient?.WholegrainsHEIFAscoreFemale
        "Meatandalternatives" -> patient?.MeatandalternativesHEIFAscoreFemale
        "Dairyandalternatives" -> patient?.DairyandalternativesHEIFAscoreFemale
        "Water" -> patient?.WaterHEIFAscoreFemale
        "UnsaturatedFat" -> patient?.UnsaturatedFatHEIFAscoreFemale
        "Sodium" -> patient?.SodiumHEIFAscoreFemale
        "Sugar" -> patient?.SugarHEIFAscoreFemale
        "Alcohol" -> patient?.AlcoholHEIFAscoreFemale
        "Discretionary" -> patient?.DiscretionaryHEIFAscoreFemale
        else -> 0f
    } ?: 0f
}