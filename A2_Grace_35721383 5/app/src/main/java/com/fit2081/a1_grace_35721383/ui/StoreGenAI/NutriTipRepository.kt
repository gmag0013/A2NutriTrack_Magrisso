package com.fit2081.a1_grace_35721383.ui.StoreGenAI

import kotlinx.coroutines.flow.Flow

class NutriTipRepository(private val tipDao: NutriTipDao) {

    suspend fun insertTip(tip: NutriTip) {
        tipDao.insertTip(tip)
    }
    suspend fun deleteTip(tip: NutriTip) {
        tipDao.deleteTip(tip)
    }

    fun getAllTips(): Flow<List<NutriTip>> {
        return tipDao.getAllTips()
    }
}