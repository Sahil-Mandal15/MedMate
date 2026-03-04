package com.sahilm.medmate.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_fact")
data class FoodFactEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    @ColumnInfo("food_fact")val foodFact: String,
    @ColumnInfo("timestamp") val timestamp: Long = System.currentTimeMillis()
)
