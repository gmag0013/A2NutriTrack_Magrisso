package com.fit2081.a1_grace_35721383.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fit2081.a1_grace_35721383.repository.PatientRepository


/*
I used Chat GPT (https://chatgpt.com/) to assist with the implementation of this class,
specifically to correctly implement the ViewModelProvider.Factory interface and instantiate my custom "PatientViewModel."
[Draft copies for this file: 2]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */


class PatientViewModelFactory(
    private val repository: PatientRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Safe due to check above
            return PatientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}