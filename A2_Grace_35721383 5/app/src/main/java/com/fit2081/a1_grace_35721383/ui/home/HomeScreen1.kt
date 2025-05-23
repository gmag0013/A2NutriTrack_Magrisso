package com.fit2081.a1_grace_35721383.ui.home
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.food.FoodIntakeQuestions
import com.fit2081.a1_grace_35721383.R
import com.fit2081.a1_grace_35721383.ui.settings.SettingsActivity
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
I used Chat GPT (https://chatgpt.com/) to a assist with the UI layout and foodscores of this screen.
[Draft copies for this file: 6]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */




data class NavItem(val label: String, val icon: ImageVector)

class HomeScreen1 : ComponentActivity() {
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
                        FirstPage()
                    }
                }
            }
        }
    }
}
@Composable
fun FoodScoreCard(title: String, subtitle: String = "", description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = subtitle, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navItems: List<NavItem>, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        navItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                label = { Text(text = navItem.label) }
            )
        }
    }
}
@Composable
fun ContentSection(context: Context) {
    val foodQualityScore = remember { mutableStateOf<String?>("...") }
    val userName = remember { mutableStateOf("...") }

    LaunchedEffect(Unit) {
        val db = AppDatabase.getInstance(context)
        val patientDao = db.patientDao()
        val sessionPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sessionPref.getInt("userId", -1)

        if (userId != -1) {
            val patient = withContext(Dispatchers.IO) {
                patientDao.getPatientOnce(userId)
            }

            patient?.let {
                userName.value = it.name.ifBlank { "User" }
                val gender = it.sex.lowercase()
                val score = when (gender) {
                    "male" -> it.HEIFAtotalscoreMale
                    "female" -> it.HEIFAtotalscoreFemale
                    else -> null
                }
                foodQualityScore.value = "${score ?: 0f} / 100"
                Log.d("ScoreDebug", "User: ${it.name}, Score: $score")
            } ?: run {
                foodQualityScore.value = "No score found"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello, ${userName.value}",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You've already filled in your Food Intake Questionnaire.\nBut you can change details here:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val sessionPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val userId = sessionPref.getInt("userId", -1)
                if (userId != -1) {
                    val intent = Intent(context, FoodIntakeQuestions::class.java)
                    intent.putExtra("userId", userId)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .height(40.dp)
                .width(200.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Edit", color = Color.White, fontSize = 18.sp)
        }

        Image(
            painter = painterResource(id = R.drawable.monash_food),
            contentDescription = "Daily Food Plate",
            modifier = Modifier
                .size(300.dp)
                .padding(10.dp)
        )

        FoodScoreCard(
            title = "My Score:",
            subtitle = "Your Food Quality Score: ${foodQualityScore.value}",
            description = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines."
        )
    }
}

@Composable
fun FirstPage() {
    val context = LocalContext.current
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Insights", Icons.Default.Info),
        NavItem("NutriCoach", Icons.Default.Face),
        NavItem("Settings", Icons.Default.Settings)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navItems, selectedIndex) { index ->
                selectedIndex = index
                when (index) {
                    1 -> context.startActivity(Intent(context, InsightsBreakdown::class.java))
                    2 -> context.startActivity(Intent(context, NutriCoach::class.java))
                    3 -> context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF5F5F5)
        ) {
            if (selectedIndex == 0) {
                ContentSection(context)
            }
        }
    }
}
