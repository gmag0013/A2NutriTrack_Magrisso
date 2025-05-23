package com.fit2081.a1_grace_35721383.ui.StoreGenAI


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutri_tips")
data class NutriTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tip: String,
    val timestamp: Long = System.currentTimeMillis()
)