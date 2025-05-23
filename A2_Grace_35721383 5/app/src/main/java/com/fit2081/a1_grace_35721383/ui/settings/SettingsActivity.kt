package com.fit2081.a1_grace_35721383.ui.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.ui.home.BottomNavigationBar
import com.fit2081.a1_grace_35721383.ui.home.HomeScreen1
import com.fit2081.a1_grace_35721383.ui.home.InsightsBreakdown
import com.fit2081.a1_grace_35721383.ui.home.NavItem
import com.fit2081.a1_grace_35721383.ui.home.NutriCoach
import com.fit2081.a1_grace_35721383.ui.login.LoginScreen
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/*
I used Chat GPT (https://chatgpt.com/) to a assist with the implementation of displaying user information and log out button.
[Draft copies for this file: 4]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */
class SettingsActivity : ComponentActivity() {
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
                        SettingsScreen(context = this@SettingsActivity)
                    }
                }
            }
        }
    }
}
@Composable
fun SettingsScreen(context: Context) {
    val sessionPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val userId = sessionPref.getInt("userId", -1)
    val db = AppDatabase.getInstance(context)
    val patientDao = db.patientDao()

    var selectedIndex by remember { mutableStateOf(3) }
    var patient by remember { mutableStateOf<Patient?>(null) }

    LaunchedEffect(Unit) {
        if (userId != -1) {
            patient = withContext(Dispatchers.IO) {
                patientDao.getPatientOnce(userId)
            }
        }
    }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Insights", Icons.Default.Info),
        NavItem("NutriCoach", Icons.Default.Face),
        NavItem("Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    when (index) {
                        0 -> context.startActivity(Intent(context, HomeScreen1::class.java))
                        1 -> context.startActivity(Intent(context, InsightsBreakdown::class.java))
                        2 -> context.startActivity(Intent(context, NutriCoach::class.java))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            patient?.let {
                Text("Name: ${it.name}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Phone Number: ${it.phoneNumber}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("User ID: ${it.userId}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
            } ?: Text("Loading user info...", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, ChangePasswordActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Change Password", color = Color.White)
            }

            Spacer(modifier = Modifier.height(260.dp))

            Button(
                onClick = {
                    sessionPref.edit().remove("userId").apply()
                    context.startActivity(Intent(context, LoginScreen::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Logout", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
