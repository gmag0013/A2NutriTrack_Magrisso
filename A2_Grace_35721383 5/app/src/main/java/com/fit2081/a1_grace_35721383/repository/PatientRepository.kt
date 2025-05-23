package com.fit2081.a1_grace_35721383.repository

import androidx.lifecycle.LiveData
import com.fit2081.a1_grace_35721383.data.Patient
import com.fit2081.a1_grace_35721383.data.PatientDao


/*
I used Chat GPT (https://chatgpt.com/) to write functions to deal with my Live Data Patient Information and used these functions in other files.
[Draft copies for this file: 1]
The output from this tool was not modified at all.
 */

class PatientRepository(private val patientDao: PatientDao) {

    fun getPatientById(userId: Int): LiveData<Patient?> {
        return patientDao.getPatientLiveDataById(userId)
    }

    suspend fun getPatientOnce(userId: Int): Patient? {
        return patientDao.getPatientOnce(userId)
    }

    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.update(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        patientDao.delete(patient)
    }
}