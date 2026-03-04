package com.sahilm.medmate.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_reminder")
data class MedicineReminderEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo("medicine_name")
    var medicineName: String,
    var description: String,
    var time: String,
    var date: String,
    var timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo("repeat_daily")
    var repeatDaily: Boolean = false,
    @ColumnInfo("is_taken")
    var isTaken: Boolean = false,
)
