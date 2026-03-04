package com.sahilm.medmate.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.domain.model.MedicineReminderModel
import com.sahilm.medmate.domain.repository.HomeRepository
import com.sahilm.medmate.utils.AlarmScheduler
import com.sahilm.medmate.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _foodFact: MutableLiveData<ResultState<FoodFactModel>> = MutableLiveData()
    val foodFact: LiveData<ResultState<FoodFactModel>> = _foodFact

    private val _medicineReminders: MutableLiveData<List<MedicineReminderModel>> = MutableLiveData()
    val medicineReminders: LiveData<List<MedicineReminderModel>> = _medicineReminders

    init {
        getFoodFact()
    }

    fun getFoodFact() {
        viewModelScope.launch {
            _foodFact.value = repository.getFoodFact()
            Log.d("TAG", "getFoodFact: $foodFact")
        }
    }

    fun addMedicineReminder(context: Context, reminder: MedicineReminderModel) {
        viewModelScope.launch {
            repository.addMedicineReminder(reminder)
            // After insert Room auto-generates the id; fetch all reminders to get the real ids
            val all = repository.getAllMedicineReminders()
            _medicineReminders.value = all
            // Schedule alarm for the newly added reminder (match by name+time+date)
            val saved = all.firstOrNull {
                it.medicineName == reminder.medicineName &&
                it.time == reminder.time &&
                it.date == reminder.date
            }
            saved?.let { AlarmScheduler.schedule(context, it) }
        }
    }

    fun getAllMedicineReminders() {
        viewModelScope.launch {
            _medicineReminders.value = repository.getAllMedicineReminders()
        }
    }

    fun deleteMedicineReminder(context: Context, reminderId: Int) {
        viewModelScope.launch {
            AlarmScheduler.cancel(context, reminderId)
            repository.deleteMedicineReminder(reminderId)
            _medicineReminders.value = repository.getAllMedicineReminders()
        }
    }

    fun markReminderAsTaken(context: Context, reminderId: Int) {
        viewModelScope.launch {
            AlarmScheduler.cancel(context, reminderId)
            repository.markReminderAsTaken(reminderId)
            _medicineReminders.value = repository.getAllMedicineReminders()
        }
    }
}