package com.sahilm.medmate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahilm.medmate.data.local.dao.FoodFactDao
import com.sahilm.medmate.data.local.dao.MedicineReminderDao
import com.sahilm.medmate.data.local.entity.FoodFactEntity
import com.sahilm.medmate.data.local.entity.MedicineReminderEntity

@Database(
    entities = [FoodFactEntity::class, MedicineReminderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MedMateDatabase: RoomDatabase() {
    abstract fun foodFactDao(): FoodFactDao
    abstract fun medicineReminderDao(): MedicineReminderDao
}