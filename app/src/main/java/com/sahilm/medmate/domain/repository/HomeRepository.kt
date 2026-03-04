package com.sahilm.medmate.domain.repository

import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.domain.model.MedicineReminderModel
import com.sahilm.medmate.utils.ResultState

interface HomeRepository {
    suspend fun getFoodFact() : ResultState<FoodFactModel>
    suspend fun addMedicineReminder(medicineReminderModel: MedicineReminderModel)
    suspend fun getAllMedicineReminders() : List<MedicineReminderModel>
    suspend fun deleteMedicineReminder(reminderId: Int)
    suspend fun markReminderAsTaken(reminderId: Int)
}