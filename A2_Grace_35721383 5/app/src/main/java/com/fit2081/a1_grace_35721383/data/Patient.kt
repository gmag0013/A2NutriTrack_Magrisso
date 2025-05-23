package com.fit2081.a1_grace_35721383.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index


/*
I used Chat GPT (https://chatgpt.com/) to organize my patient table and write the PrimaryKeys correctly.
[Draft copies for this file: 2]
The output from this tool was not drastically modified.
 */

@Entity(
    tableName = "patient_table",
    indices = [Index(value = ["userId"])]
)
data class Patient(
    @PrimaryKey val userId: Int,
    val phoneNumber: String = "",
    val name: String = "",
    val sex: String = "unspecified",

    val password: String? = null,
    val isClaimed: Boolean = false,

    //  HEIFA Total Scores
    val HEIFAtotalscoreMale: Float? = null,
    val HEIFAtotalscoreFemale: Float? = null,

    //  Discretionary Foods
    val DiscretionaryHEIFAscoreMale: Float? = null,
    val DiscretionaryHEIFAscoreFemale: Float? = null,
    val Discretionaryservesize: Float? = null,

    //  Vegetables & Legumes
    val VegetablesHEIFAscoreMale: Float? = null,
    val VegetablesHEIFAscoreFemale: Float? = null,
    val Vegetableswithlegumesallocatedservesize: Float? = null,
    val LegumesallocatedVegetables: Float? = null,
    val Vegetablesvariationsscore: Float? = null,
    val VegetablesCruciferous: Float? = null,
    val VegetablesTuberandbulb: Float? = null,
    val VegetablesOther: Float? = null,
    val Legumes: Float? = null,
    val VegetablesGreen: Float? = null,
    val VegetablesRedandorange: Float? = null,

    //  Fruit
    val FruitHEIFAscoreMale: Float? = null,
    val FruitHEIFAscoreFemale: Float? = null,
    val Fruitservesize: Float? = null,
    val Fruitvariationsscore: Float? = null,
    val FruitPome: Float? = null,
    val FruitTropicalandsubtropical: Float? = null,
    val FruitBerry: Float? = null,
    val FruitStone: Float? = null,
    val FruitCitrus: Float? = null,
    val FruitOther: Float? = null,

    //  Grains & Cereals
    val GrainsandcerealsHEIFAscoreMale: Float? = null,
    val GrainsandcerealsHEIFAscoreFemale: Float? = null,
    val Grainsandcerealsservesize: Float? = null,
    val GrainsandcerealsNonwholegrains: Float? = null,
    val WholegrainsHEIFAscoreMale: Float? = null,
    val WholegrainsHEIFAscoreFemale: Float? = null,
    val Wholegrainsservesize: Float? = null,

    //  Meat & Alternatives
    val MeatandalternativesHEIFAscoreMale: Float? = null,
    val MeatandalternativesHEIFAscoreFemale: Float? = null,
    val Meatandalternativeswithlegumesallocatedservesize: Float? = null,
    val LegumesallocatedMeatandalternatives: Float? = null,

    //  Dairy & Alternatives
    val DairyandalternativesHEIFAscoreMale: Float? = null,
    val DairyandalternativesHEIFAscoreFemale: Float? = null,
    val Dairyandalternativesservesize: Float? = null,

    //  Sodium
    val SodiumHEIFAscoreMale: Float? = null,
    val SodiumHEIFAscoreFemale: Float? = null,
    val Sodiummgmilligrams: Float? = null,

    // Alcohol
    val AlcoholHEIFAscoreMale: Float? = null,
    val AlcoholHEIFAscoreFemale: Float? = null,
    val Alcoholstandarddrinks: Float? = null,

    // Water & Beverages
    val WaterHEIFAscoreMale: Float? = null,
    val WaterHEIFAscoreFemale: Float? = null,
    val Water: Float? = null,
    val WaterTotalmL: Float? = null,
    val BeverageTotalmL: Float? = null,

    // Sugar
    val SugarHEIFAscoreMale: Float? = null,
    val SugarHEIFAscoreFemale: Float? = null,
    val Sugar: Float? = null,

    // Saturated Fat
    val SaturatedFatHEIFAscoreMale: Float? = null,
    val SaturatedFatHEIFAscoreFemale: Float? = null,
    val SaturatedFat: Float? = null,

    // Unsaturated Fat
    val UnsaturatedFatHEIFAscoreMale: Float? = null,
    val UnsaturatedFatHEIFAscoreFemale: Float? = null,
    val UnsaturatedFatservesize: Float? = null
)