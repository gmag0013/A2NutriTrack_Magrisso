package com.fit2081.a1_grace_35721383.ui.StoreGenAI

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.a1_grace_35721383.common.AppDatabase
import com.fit2081.a1_grace_35721383.ui.home.NutriCoach
import com.fit2081.a1_grace_35721383.ui.theme.A1_Grace_35721383Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/*
I used Chat GPT (https://chatgpt.com/) to a assist with the implementation of adding cards of the GENAI results to a list.
The user also has the ability to delete the cards from their saved list.
[Draft copies for this file: 6]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */

class NutriTipsScreenActivity : ComponentActivity() {
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
                        NutriTipsScreenUI()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriTipsScreenUI() {
    val context = LocalContext.current
    val viewModel: NutriTipViewModel = viewModel(
        factory = NutriTipViewModelFactory(
            NutriTipRepository(AppDatabase.getInstance(context).nutriTipDao())
        )
    )

    val allTips by viewModel.allTips.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Saved NutriTips") },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, NutriCoach::class.java))
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (allTips.isEmpty()) {
                Text(
                    text = "No tips saved yet.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 40.dp)
                )
            } else {
                allTips.forEach { tip ->
                    NutriTipCard(
                        tip = tip,
                        onDeleteConfirmed = { viewModel.deleteTip(it)}
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun NutriTipCard(tip: NutriTip, onDeleteConfirmed: (NutriTip) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this tip?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirmed(tip)
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = tip.tip, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(tip.timestamp)),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Bottom-right trash icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Tip",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}