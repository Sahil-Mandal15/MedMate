package com.sahilm.medmate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sahilm.medmate.data.dto.FoodFactResponse
import com.sahilm.medmate.data.local.entity.FoodFactEntity

@Dao
interface FoodFactDao {
    @Query("SELECT * FROM food_fact WHERE id = 1 LIMIT 1")
    fun getFoodFact(): FoodFactEntity?

    @Upsert
    fun addFoodFact(foodFact: FoodFactEntity)


}