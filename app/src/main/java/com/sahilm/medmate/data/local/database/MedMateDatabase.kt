package com.sahilm.medmate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahilm.medmate.data.local.dao.FoodFactDao
import com.sahilm.medmate.data.local.entity.FoodFactEntity

@Database(
    entities = [FoodFactEntity::class],
    version = 1
)
abstract class MedMateDatabase: RoomDatabase() {
    abstract fun foodFactDao(): FoodFactDao
}