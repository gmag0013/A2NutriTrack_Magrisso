package com.fit2081.a1_grace_35721383.ui.StoreGenAI

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NutriTipDao {

    @Insert
    suspend fun insertTip(tip: NutriTip)

    @Delete
    suspend fun deleteTip(tip: NutriTip)

    @Query("SELECT * FROM nutri_tips ORDER BY timestamp DESC")
    fun getAllTips(): Flow<List<NutriTip>>
}