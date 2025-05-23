package com.fit2081.a1_grace_35721383.common

import android.content.Context
import android.util.Log
import com.fit2081.a1_grace_35721383.data.Patient
import java.io.BufferedReader
import java.io.InputStreamReader


/*
I used Chat GPT (https://chatgpt.com/) to write code that helps read the data1.csv file and extract the scores needed for the spec requirements that are found in
insights breakdown file. In addition, ChatGPT wrote LOG messages to catch errors of data not loading properly.
[Draft copies for this file: 2]
The output from this tool was not drastically modified.
 */

object DataManager {

    suspend fun loadPatientDataFromCsv(context: Context): List<Patient> {
        val patients = mutableListOf<Patient>()

        try {
            context.assets.open("data1.csv").use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = reader.readLines()

                lines.drop(1).forEach { line ->
                    val tokens = line.split(",")

                    Log.d("DataManager", "Parsing line: $line")

                    if (tokens.size < 63) {
                        Log.e("DataManager", "Skipped line — only ${tokens.size} columns.")
                        return@forEach
                    }

                    val phoneNumber = tokens[0].trim()
                    val userId = tokens[1].trim().toIntOrNull() ?: return@forEach
                    val sex = tokens[2].trim()
                    val name = ""

                    val patient = Patient(
                        userId = userId,
                        phoneNumber = phoneNumber,
                        name = name,
                        sex = sex,
                        HEIFAtotalscoreMale = tokens[3].toFloatOrNull(),
                        HEIFAtotalscoreFemale = tokens[4].toFloatOrNull(),
                        DiscretionaryHEIFAscoreMale = tokens[5].toFloatOrNull(),
                        DiscretionaryHEIFAscoreFemale = tokens[6].toFloatOrNull(),
                        Discretionaryservesize = tokens[7].toFloatOrNull(),
                        VegetablesHEIFAscoreMale = tokens[8].toFloatOrNull(),
                        VegetablesHEIFAscoreFemale = tokens[9].toFloatOrNull(),
                        Vegetableswithlegumesallocatedservesize = tokens[10].toFloatOrNull(),
                        LegumesallocatedVegetables = tokens[11].toFloatOrNull(),
                        Vegetablesvariationsscore = tokens[12].toFloatOrNull(),
                        VegetablesCruciferous = tokens[13].toFloatOrNull(),
                        VegetablesTuberandbulb = tokens[14].toFloatOrNull(),
                        VegetablesOther = tokens[15].toFloatOrNull(),
                        Legumes = tokens[16].toFloatOrNull(),
                        VegetablesGreen = tokens[17].toFloatOrNull(),
                        VegetablesRedandorange = tokens[18].toFloatOrNull(),
                        FruitHEIFAscoreMale = tokens[19].toFloatOrNull(),
                        FruitHEIFAscoreFemale = tokens[20].toFloatOrNull(),
                        Fruitservesize = tokens[21].toFloatOrNull(),
                        Fruitvariationsscore = tokens[22].toFloatOrNull(),
                        FruitPome = tokens[23].toFloatOrNull(),
                        FruitTropicalandsubtropical = tokens[24].toFloatOrNull(),
                        FruitBerry = tokens[25].toFloatOrNull(),
                        FruitStone = tokens[26].toFloatOrNull(),
                        FruitCitrus = tokens[27].toFloatOrNull(),
                        FruitOther = tokens[28].toFloatOrNull(),
                        GrainsandcerealsHEIFAscoreMale = tokens[29].toFloatOrNull(),
                        GrainsandcerealsHEIFAscoreFemale = tokens[30].toFloatOrNull(),
                        Grainsandcerealsservesize = tokens[31].toFloatOrNull(),
                        GrainsandcerealsNonwholegrains = tokens[32].toFloatOrNull(),
                        WholegrainsHEIFAscoreMale = tokens[33].toFloatOrNull(),
                        WholegrainsHEIFAscoreFemale = tokens[34].toFloatOrNull(),
                        Wholegrainsservesize = tokens[35].toFloatOrNull(),
                        MeatandalternativesHEIFAscoreMale = tokens[36].toFloatOrNull(),
                        MeatandalternativesHEIFAscoreFemale = tokens[37].toFloatOrNull(),
                        Meatandalternativeswithlegumesallocatedservesize = tokens[38].toFloatOrNull(),
                        LegumesallocatedMeatandalternatives = tokens[39].toFloatOrNull(),
                        DairyandalternativesHEIFAscoreMale = tokens[40].toFloatOrNull(),
                        DairyandalternativesHEIFAscoreFemale = tokens[41].toFloatOrNull(),
                        Dairyandalternativesservesize = tokens[42].toFloatOrNull(),
                        SodiumHEIFAscoreMale = tokens[43].toFloatOrNull(),
                        SodiumHEIFAscoreFemale = tokens[44].toFloatOrNull(),
                        Sodiummgmilligrams = tokens[45].toFloatOrNull(),
                        AlcoholHEIFAscoreMale = tokens[46].toFloatOrNull(),
                        AlcoholHEIFAscoreFemale = tokens[47].toFloatOrNull(),
                        Alcoholstandarddrinks = tokens[48].toFloatOrNull(),
                        WaterHEIFAscoreMale = tokens[49].toFloatOrNull(),
                        WaterHEIFAscoreFemale = tokens[50].toFloatOrNull(),
                        Water = tokens[51].toFloatOrNull(),
                        WaterTotalmL = tokens[52].toFloatOrNull(),
                        BeverageTotalmL = tokens[53].toFloatOrNull(),
                        SugarHEIFAscoreMale = tokens[54].toFloatOrNull(),
                        SugarHEIFAscoreFemale = tokens[55].toFloatOrNull(),
                        Sugar = tokens[56].toFloatOrNull(),
                        SaturatedFatHEIFAscoreMale = tokens[57].toFloatOrNull(),
                        SaturatedFatHEIFAscoreFemale = tokens[58].toFloatOrNull(),
                        SaturatedFat = tokens[59].toFloatOrNull(),
                        UnsaturatedFatHEIFAscoreMale = tokens[60].toFloatOrNull(),
                        UnsaturatedFatHEIFAscoreFemale = tokens[61].toFloatOrNull(),
                        UnsaturatedFatservesize = tokens[62].toFloatOrNull(),
                        password = null,
                        isClaimed = false
                    )

                    patients.add(patient)
                }
            }
        } catch (e: Exception) {
            Log.e("DataManager", "Failed to load CSV: ${e.message}")
            e.printStackTrace()
        }

        return patients
    }
}