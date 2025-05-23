package com.fit2081.a1_grace_35721383.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.Image
import com.fit2081.a1_grace_35721383.R
import com.fit2081.a1_grace_35721383.ui.home.HomeScreen1
import com.fit2081.a1_grace_35721383.ui.login.LoginScreen
import kotlinx.coroutines.withContext


/*
I used Chat GPT (https://chatgpt.com/) to write toast messages to check if my data and patients loaded correctly.
[Draft copies for this file: 1]
The output from this tool was not drastically modified but I removed some of its excessive use of emojis in this file.
 */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("MainActivity", "First run detected, loading CSV...")
                val patients = DataManager.loadPatientDataFromCsv(this@MainActivity)

                Log.d("MainActivity", "Found ${patients.size} patients in CSV")
                patients.forEach {
                    Log.d("MainActivity", "Inserting Patient: ${it.userId} - ${it.phoneNumber}")
                }

                db.patientDao().insertAll(patients)

                val all = db.patientDao().getAllPatients()
                Log.d("MainActivity", "Patients in DB after insert: ${all.size}")

                sharedPref.edit().putBoolean("isFirstRun", false).apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Inserted ${patients.size} patients!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Auto-login check
                    val sessionPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val loggedInUserId = sessionPref.getInt("userId", -1)

                    if (loggedInUserId != -1) {
                        Log.d("MainActivity", "User already logged in. Redirecting to Home.")
                        startActivity(Intent(this@MainActivity, HomeScreen1::class.java))
                        finish()
                    }
                }
            }
        } else {
            // Regular launch auto-login
            val sessionPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val loggedInUserId = sessionPref.getInt("userId", -1)

            if (loggedInUserId != -1) {
                Log.d("MainActivity", "User already logged in. Redirecting to Home.")
                startActivity(Intent(this@MainActivity, HomeScreen1::class.java))
                finish()
                return
            }
        }

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
                        WelcomeScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NutriTrack",
            style = TextStyle(fontSize = 40.sp),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.nutri_logo),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app provides general health and nutrition information for " +
                    "educational purposes only. It is not intended as medical advice, " +
                    "diagnosis, or treatment. Always consult a qualified healthcare " +
                    "professional before making any changes to your diet, exercise, or " +
                    "health regimen. Use this app at your own risk. " +
                    "If you’d like to see an Accredited Practicing Dietitian (APD), please " +
                    "visit the Monash Nutrition/Dietetics Clinic (discounted rates for students): " +
                    "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp),
            fontFamily = FontFamily.SansSerif,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                context.startActivity(Intent(context, LoginScreen::class.java))
            },
            modifier = Modifier
                .height(55.dp)
                .width(320.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Login", color = Color.White, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Made by Grace Magrisso (35721383)",
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            style = TextStyle(fontSize = 12.sp),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

fun testPrintAllFoodIntakes(db: AppDatabase, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val foodIntakeDao = db.foodIntakeDao()
        val allFoodIntakes = foodIntakeDao.getAllFoodIntakes()

        Log.d("FoodIntake", "Found ${allFoodIntakes.size} entries")
        for (intake in allFoodIntakes) {
            Log.d("FoodIntake", intake.toString())
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "Loaded ${allFoodIntakes.size} food intake entries!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}