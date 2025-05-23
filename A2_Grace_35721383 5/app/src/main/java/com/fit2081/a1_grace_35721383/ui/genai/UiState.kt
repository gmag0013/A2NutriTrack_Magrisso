package com.fit2081.a1_grace_35721383.ui.genai


/*
I used Chat GPT (https://chatgpt.com/) to assist with structuring the sealed class used for handling different
UI states in the GENAI Screen.
[Draft copies for this file: 2]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */

//Represents the different UI states for the GenAI screen.
sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val outputText: String) : UiState()
    data class Error(val errorMessage: String) : UiState()
}