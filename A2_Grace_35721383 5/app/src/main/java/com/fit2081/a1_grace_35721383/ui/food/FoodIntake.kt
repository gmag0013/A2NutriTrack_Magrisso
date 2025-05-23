package com.fit2081.a1_grace_35721383.ui.food

import androidx.room.*
import com.fit2081.a1_grace_35721383.data.Patient
/*
I used Chat GPT (https://chatgpt.com/) to write this food intake table from the users' answers from the questionnaire.
[Draft copies for this file: 1]
The output from this tool was not modified at all.
 */
@Entity(
    tableName = "food_intake_table",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Foreign key linking to the patient
    val userId: Int,

    // Persona selected via modal or dropdown
    val selectedPersona: String? = null,
    val dropdownPersona: String? = null,

    // Comma-separated string of selected foods
    val foodSelections: String? = null,

    // Time values for behavioral patterns
    val timeBiggestMeal: String? = null,  // Format: "HH:mm"
    val timeSleep: String? = null,        // Format: "HH:mm"
    val timeWake: String? = null          // Format: "HH:mm"
)