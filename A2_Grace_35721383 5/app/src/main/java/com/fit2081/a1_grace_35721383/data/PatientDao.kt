package com.fit2081.a1_grace_35721383.data

import androidx.lifecycle.LiveData
import androidx.room.*

/*
I used Chat GPT (https://chatgpt.com/) to write this file of code. It also wrote the comments to keep the file organized and clear.
[Draft copies for this file: 5]
The output from this tool was not modified at all.
 */

@Dao
interface PatientDao {

    /** Insert or replace a single patient */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    /** Insert or replace a list of patients */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    /** Get all patients (non-LiveData, one-time use) */
    @Query("SELECT * FROM patient_table")
    suspend fun getAllPatients(): List<Patient>

    /** Observe a patient by ID as LiveData (for UI binding) */
    @Query("SELECT * FROM patient_table WHERE userId = :userId")
    fun getPatientLiveDataById(userId: Int): LiveData<Patient?>

    /** Get a patient by ID (for use in suspend/coroutine context) */
    @Query("SELECT * FROM patient_table WHERE userId = :userId")
    suspend fun getPatientOnce(userId: Int): Patient?

    /** Update a full patient record */
    @Update
    suspend fun update(patient: Patient)

    /** Delete a patient record */
    @Delete
    suspend fun delete(patient: Patient)

    /** Update only name and password, and mark as claimed */
    @Query("""
        UPDATE patient_table 
        SET name = :name, password = :password, isClaimed = 1 
        WHERE userId = :userId
    """)
    suspend fun updateNameAndPassword(userId: Int, name: String, password: String)
}
