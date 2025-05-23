package com.fit2081.a1_grace_35721383.ui.genai

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a1_grace_35721383.R
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.repository.PatientRepository
import com.fit2081.a1_grace_35721383.ui.StoreGenAI.*
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModel
import com.fit2081.a1_grace_35721383.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder

/*
I used Chat GPT (https://chatgpt.com/) to change the layout of the GenAI portion on the NutriCoach activity.
[Draft copies for this file: 10]
The output from this tool was changed to reflect the assignment specs, prompting the user with their information for fruit help.
 */

@Composable
fun GenAIScreen(
    defaultPrompt: String = "Generate a short encouraging message to help someone improve their fruit intake.",
    genAiViewModel: GenAiViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by genAiViewModel.uiState.collectAsState()

    val db = remember { AppDatabase.getInstance(context) }

    val nutriTipViewModel: NutriTipViewModel = viewModel(
        factory = NutriTipViewModelFactory(NutriTipRepository(db.nutriTipDao()))
    )

    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(PatientRepository(db.patientDao()))
    )

    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    var prompt by rememberSaveable { mutableStateOf(defaultPrompt) }
    var result by rememberSaveable { mutableStateOf("") }
    var showSaveIcon by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != -1) {
            val patient = withContext(Dispatchers.IO) { patientViewModel.getPatientOnce(userId) }
            patient?.let {
                val name = it.name.ifBlank { "this person" }
                val gender = it.sex.lowercase().trim()
                val score = if (gender == "male") it.FruitHEIFAscoreMale else it.FruitHEIFAscoreFemale
                val roundedScore = score?.let { s -> "%.1f".format(s) } ?: "unknown"
                prompt = "$name (a $gender with a fruit HEIFA score of $roundedScore): " +
                        "Give a short and encouraging message with a fruit habit, tip, or snack idea."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("NutriCoach AI", style = MaterialTheme.typography.titleLarge)

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Prompt") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    showSaveIcon = true
                    isSaved = false
                    genAiViewModel.sendPrompt(prompt)
                },
                enabled = prompt.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterVertically),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Ask Gemini", color = Color.White)
            }
        }

        Button(
            onClick = {
                context.startActivity(Intent(context, NutriTipsScreenActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("My Saved Tips", color = Color.White)
        }

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (uiState is UiState.Success || uiState is UiState.Error) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                result = when (uiState) {
                    is UiState.Success -> (uiState as UiState.Success).outputText
                    is UiState.Error -> (uiState as UiState.Error).errorMessage
                    else -> ""
                }

                Text(
                    text = result,
                    color = if (uiState is UiState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (uiState is UiState.Success && result.isNotBlank() && showSaveIcon) {
                    IconButton(onClick = {
                        nutriTipViewModel.addTip(result)
                        isSaved = true
                        showSaveIcon = false
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Tip",
                            tint = if (isSaved) Color(0xFF6200EE) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}