package com.sahilm.medmate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sahilm.medmate.data.local.entity.MedicineReminderEntity

@Dao
interface MedicineReminderDao {

    @Upsert
    fun addMedicineReminder(medicineReminderEntity: MedicineReminderEntity)

    @Query("SELECT * FROM medicine_reminder WHERE is_taken = 0 ORDER BY timestamp ASC")
    fun getAllMedicineReminders(): List<MedicineReminderEntity>

    @Query("UPDATE medicine_reminder SET is_taken = 1 WHERE id = :reminderId")
    fun markReminderAsTaken(reminderId: Int)

    @Query("DELETE FROM medicine_reminder WHERE id = :reminderId")
    fun deleteMedicineReminder(reminderId: Int)
}