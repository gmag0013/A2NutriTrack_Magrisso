package com.fit2081.a1_grace_35721383.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.onboarding.ClaimAccountActivity
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader


/*
I used Chat GPT (https://chatgpt.com/) to validate the users' ids and phone numbers from the dataset.
[Draft copies for this file: 1]
The output was modified to fit the rest of my codebase but the code structure suggested by CHATGPT was not drastically modified.
 */

class LoginScreen : ComponentActivity() {
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
                        LoginScreenUI(this@LoginScreen)
                    }
                }
            }
        }
    }
}

fun getAllIdPhonePairs(context: Context): List<Pair<String, String>> {
    val fileName = "data1.csv"
    val idPhonePairs = mutableListOf<Pair<String, String>>()
    try {
        context.assets.open(fileName).bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(",")
                if (values.size >= 2) {
                    val phoneNumber = values[0].trim()
                    val id = values[1].trim()
                    idPhonePairs.add(Pair(id, phoneNumber))
                }
            }
        }
    } catch (e: Exception) {
        Log.e("LoginScreen", "Error reading CSV", e)
    }
    return idPhonePairs
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return phoneNumber.all { it.isDigit() } && phoneNumber.length in 10..15
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenUI(context: Context) {
    val idPhonePairs = remember { getAllIdPhonePairs(context) }
    val idOptions = idPhonePairs.map { it.first }

    var selectedId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    var idError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log in",
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )

        Text(
            text = "This app is only for pre-registered users.\nPlease have your ID and phone number handy before continuing.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedId,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select ID") },
                isError = idError,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                idOptions.forEach { id ->
                    DropdownMenuItem(
                        text = { Text(id) },
                        onClick = {
                            selectedId = id
                            expanded = false
                        }
                    )
                }
            }
        }

        if (idError) {
            Text(
                text = "Please select an ID",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneNumberError = !isValidPhoneNumber(it)
            },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phoneNumberError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (phoneNumberError) {
            Text(
                text = "Invalid Phone Number",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                idError = selectedId.isBlank()
                phoneNumberError = !isValidPhoneNumber(phoneNumber)
                loginError = false

                if (!idError && !phoneNumberError) {
                    val isValidLogin = idPhonePairs.any { (id, phone) ->
                        id == selectedId && phone == phoneNumber
                    }

                    if (isValidLogin) {
                        val realUserId = selectedId.toInt()
                        val db = AppDatabase.getInstance(context)
                        val patientDao = db.patientDao()

                        CoroutineScope(Dispatchers.IO).launch {
                            val patient = patientDao.getPatientOnce(realUserId)

                            withContext(Dispatchers.Main) {
                                val intent = if (patient?.isClaimed == true) {
                                    Intent(context, PasswordLoginActivity::class.java)
                                } else {
                                    Intent(context, ClaimAccountActivity::class.java)
                                }
                                intent.putExtra("userId", realUserId)
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        loginError = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (loginError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ID and Phone Number do not match.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
