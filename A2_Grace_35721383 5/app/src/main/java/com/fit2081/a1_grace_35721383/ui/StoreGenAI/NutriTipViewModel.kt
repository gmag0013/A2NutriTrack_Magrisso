package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/*
I used Chat GPT (https://chatgpt.com/) to assist with the implementation of the NutriTipViewModel class,
including setting up the StateFlow to observe and insert tips into the Room database.
[Draft copies for this file: 1]
The output was lightly adapted to fit the rest of my codebase but the core structure suggested
by ChatGPT remained largely unchanged.
 */

class NutriTipViewModel(private val repository: NutriTipRepository) : ViewModel() {

    val allTips: StateFlow<List<NutriTip>> = repository
        .getAllTips()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTip(tipText: String) {
        val tip = NutriTip(tip = tipText)
        viewModelScope.launch {
            repository.insertTip(tip)
        }

    }
    fun deleteTip(tip: NutriTip) {
        viewModelScope.launch {
            repository.deleteTip(tip)
        }
    }
}