@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.fit2081.a1_grace_35721383.ui.food

import android.app.TimePickerDialog
import android.content.Context
import com.fit2081.a1_grace_35721383.ui.food.TimePickerColumn
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.R
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.home.HomeScreen1
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import java.util.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import com.fit2081.a1_grace_35721383.data.Patient
/*
I used Chat GPT (https://chatgpt.com/) to format the UI layout, create image pop-us of the personas, write toast messages inside a coroutine to show success/failure
notifications when saving food intake data.
[Draft copies for this file: 3]
The output from this tool was not drastically modified.
 */

class FoodIntakeQuestions : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("userId", -1)

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
                        FoodIntakeScreen(userId = userId)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodIntakeScreen(userId: Int) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserFoodIntake", Context.MODE_PRIVATE)
    val sessionPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    val foodOptions = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry")
    val personaOptions = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    data class Persona(val name: String, val description: String, val imageRes: Int)

    val personas = listOf(
        Persona("Health Devotee", "I’m passionate about healthy eating & health plays a big part in my life.", R.drawable.persona1),
        Persona("Mindful Eater", "I’m health-conscious and being healthy and eating healthy is important to me.", R.drawable.persona2),
        Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes).", R.drawable.persona3),
        Persona("Balance Seeker", "I try to live a balanced lifestyle, and I think that all foods are okay in moderation.", R.drawable.persona4),
        Persona("Health Procrastinator", "I’m contemplating healthy eating but it’s not a priority for me right now.", R.drawable.persona5),
        Persona("Food Carefree", "I’m not bothered about healthy eating.", R.drawable.persona6)
    )

    var selectedPersona by remember { mutableStateOf<Persona?>(null) }
    var infoDialogPersona by remember { mutableStateOf<Persona?>(null) }

    val checkboxes = remember { mutableStateListOf(*Array(foodOptions.size) { false }) }

    val time1 = remember { mutableStateOf("") }
    val time2 = remember { mutableStateOf("") }
    val time3 = remember { mutableStateOf("") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var dropdownSelectedPersona by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        val resolvedUserId = userId.takeIf { it != -1 } ?: sessionPref.getInt("userId", -1)
        if (resolvedUserId != -1) {
            val db = AppDatabase.getInstance(context)
            val dao = db.foodIntakeDao()
            val existingData = withContext(Dispatchers.IO) {
                dao.getFoodIntakeByUserId(resolvedUserId)
            }

            existingData?.let { intake ->
                selectedPersona = personas.firstOrNull { it.name == intake.selectedPersona } ?: selectedPersona
                dropdownSelectedPersona = intake.dropdownPersona ?: ""
                intake.foodSelections?.split(",")?.forEach { food ->
                    val index = foodOptions.indexOf(food.trim())
                    if (index in foodOptions.indices) checkboxes[index] = true
                }
                time1.value = intake.timeBiggestMeal ?: ""
                time2.value = intake.timeSleep ?: ""
                time3.value = intake.timeWake ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Food Intake Questionnaire", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Persona types", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Text("Read and select a persona that best fits you", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.heightIn(max = 400.dp)) {
            items(personas.size) { index ->
                val persona = personas[index]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = painterResource(id = persona.imageRes),
                        contentDescription = persona.name,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = persona.name, fontWeight = FontWeight.Medium)
                    IconButton(onClick = { infoDialogPersona = persona }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            }
        }

        infoDialogPersona?.let { persona ->
            AlertDialog(
                onDismissRequest = { infoDialogPersona = null },
                title = { Text(text = persona.name) },
                text = { Text(text = persona.description) },
                confirmButton = {
                    TextButton(onClick = { infoDialogPersona = null }) {
                        Text("Close")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
        ) {
            OutlinedTextField(
                value = dropdownSelectedPersona,
                onValueChange = {},
                readOnly = true,
                label = { Text("Persona") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                personaOptions.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            dropdownSelectedPersona = label
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select foods you eat:", fontWeight = FontWeight.Bold)
        foodOptions.forEachIndexed { index, food ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checkboxes[index],
                    onCheckedChange = { checkboxes[index] = it }
                )
                Text(food)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select the approximate times you...", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            TimePickerColumn("Eat your biggest meal", time1)
            TimePickerColumn("Go to sleep", time2)
            TimePickerColumn("Wake up", time3)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            with(sharedPref.edit()) {
                putString("selectedPersona", selectedPersona?.name)
                foodOptions.forEachIndexed { index, _ ->
                    putBoolean("food_$index", checkboxes[index])
                }
                putString("time1", time1.value)
                putString("time2", time2.value)
                putString("time3", time3.value)
                putString("dropdownPersona", dropdownSelectedPersona)
                apply()
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resolvedUserId = userId.takeIf { it != -1 } ?: sessionPref.getInt("userId", -1)

                    if (resolvedUserId == -1) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: No user logged in.", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val db = AppDatabase.getInstance(context)
                    val patientDao = db.patientDao()
                    val foodIntakeDao = db.foodIntakeDao()

                    val patient = patientDao.getPatientOnce(resolvedUserId)
                    if (patient == null) {
                        val newPatient = Patient(userId = resolvedUserId, name = "User", password = "", isClaimed = true)
                        patientDao.insertPatient(newPatient)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "New patient created.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val selectedFoodsString = foodOptions.filterIndexed { index, _ -> checkboxes[index] }.joinToString(",")

                    val foodIntake = FoodIntake(
                        userId = resolvedUserId,
                        selectedPersona = selectedPersona?.name,
                        dropdownPersona = dropdownSelectedPersona,
                        foodSelections = selectedFoodsString,
                        timeBiggestMeal = time1.value,
                        timeSleep = time2.value,
                        timeWake = time3.value
                    )

                    foodIntakeDao.insertFoodIntake(foodIntake)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Food Intake Saved!", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, HomeScreen1::class.java))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Save Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }
}

@Composable
fun TimePickerColumn(label: String, timeState: MutableState<String>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            timeState.value = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = label, fontSize = 14.sp)
        Button(
            onClick = { timePickerDialog.show() },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("Select", fontSize = 12.sp)
        }
        Text(text = timeState.value, fontSize = 12.sp)
    }
}