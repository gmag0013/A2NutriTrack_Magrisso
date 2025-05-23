package com.fit2081.a1_grace_35721383.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.repository.PatientRepository
import com.fit2081.a1_grace_35721383.ui.genai.GenAIScreen
import com.fit2081.a1_grace_35721383.ui.home.*
import com.fit2081.a1_grace_35721383.ui.home.InsightsBreakdown
import com.fit2081.a1_grace_35721383.ui.settings.SettingsActivity
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModel
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


/*
I used Chat GPT (https://chatgpt.com/) to a assist with the implementation of GEN AI, formatting the scrollable screen, and
linking the Saved Responses to the SavedTips activity.
[Draft copies for this file: 8]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */

class NutriCoach : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A1_Grace_35721383Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White
                    ) {
                        NutriCoachScreen()
                    }
                }
            }
        }
    }
}
@Composable
fun NutriCoachScreen() {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(2) }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Insights", Icons.Default.Info),
        NavItem("NutriCoach", Icons.Default.Face),
        NavItem("Settings", Icons.Default.Settings)
    )

    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)
    val viewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(
            PatientRepository(AppDatabase.getInstance(context).patientDao())
        )
    )

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.loadPatient(userId)
        }
    }

    val patient by viewModel.patient.observeAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navItems = navItems, selectedIndex = selectedIndex) { index ->
                selectedIndex = index
                when (index) {
                    0 -> context.startActivity(Intent(context, HomeScreen1::class.java))
                    1 -> context.startActivity(Intent(context, InsightsBreakdown::class.java))
                    3 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    Text(
                        text = "Welcome to NutriCoach",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (patient != null) {
                    val name = patient?.name?.ifBlank { null } ?: "your profile"
                    val gender = patient?.sex?.lowercase()?.trim() ?: "user"
                    val score = when (gender) {
                        "male" -> patient?.FruitHEIFAscoreMale ?: 0f
                        "female" -> patient?.FruitHEIFAscoreFemale ?: 0f
                        else -> 0f
                    }

                    FruityViceSection(patient = patient!!, gender = gender)

                    Text(
                        text = "Get an inspirational message",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    val prompt = "$name ($gender, fruit score $score): Give a short encouraging message with a healthy fruit habit, tip, or snack idea."

                    GenAIScreen(defaultPrompt = prompt)
                } else {
                    Text("Loading patient data...", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun FruityViceSection(patient: Patient, gender: String) {
    val context = LocalContext.current
    var fruitName by remember { mutableStateOf("") }
    var fruitInfo by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val isFruitScoreOptimal = when (gender.lowercase()) {
        "male" -> (patient.FruitHEIFAscoreMale ?: 0f) >= 5f
        "female" -> (patient.FruitHEIFAscoreFemale ?: 0f) >= 5f
        else -> false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFruitScoreOptimal) {
                Text(
                    text = "Great job! Your fruit intake is optimal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF388E3C),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Image(
                    painter = rememberAsyncImagePainter("https://picsum.photos/400/200"),
                    contentDescription = "Random fruit image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Text("Enter a fruit to learn more:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fruitName,
                    onValueChange = {
                        fruitName = it
                        error = false
                    },
                    label = { Text("Fruit name (e.g. apple)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if (fruitName.isBlank()) {
                        error = true
                        fruitInfo = null
                        return@Button
                    }

                    coroutineScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                URL("https://www.fruityvice.com/api/fruit/${fruitName.lowercase()}").readText()
                            }

                            val json = JSONObject(response)
                            val name = json.getString("name")
                            val family = json.getString("family")
                            val sugar = json.getJSONObject("nutritions").getDouble("sugar")
                            val calories = json.getJSONObject("nutritions").getDouble("calories")
                            val protein = json.getJSONObject("nutritions").getDouble("protein")

                            fruitInfo = "$name\nFamily: $family\nSugar: $sugar g\nCalories: $calories kcal\nProtein: $protein g"
                            error = false
                        } catch (e: Exception) {
                            fruitInfo = null
                            error = true
                        }
                    }
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ){
                    Text("Get Info", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    fruitInfo != null -> {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp)
                                .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                        ) {
                            Text(
                                text = fruitInfo!!,
                                fontSize = 16.sp
                            )
                        }
                    }

                    error -> {
                        Text("No data found. Please check the fruit name.", color = Color.Red)
                    }
                }
            }
        }
    }
}
