package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/*
I used Chat GPT (https://chatgpt.com/) to assist with the implementation of the NutriTipViewModelFactory class,
specifically to correctly override the "create" method for custom ViewModel instantiation.
[Draft copies for this file: 2]
The implementation closely follows the standard factory pattern
recommended by Android, with minor naming adjustments to match my project structure.
 */


class NutriTipViewModelFactory(
    private val repository: NutriTipRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutriTipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutriTipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}