package com.sahilm.medmate.data.repository

import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.data.local.dao.FoodFactDao
import com.sahilm.medmate.data.local.dao.MedicineReminderDao
import com.sahilm.medmate.data.local.entity.FoodFactEntity
import com.sahilm.medmate.data.local.entity.MedicineReminderEntity
import com.sahilm.medmate.data.remote.FoodFactApi
import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.domain.model.MedicineReminderModel
import com.sahilm.medmate.domain.repository.HomeRepository
import com.sahilm.medmate.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TWENTY_FOUR_HOURS_IN_MILLIS = 24 * 60 * 60 * 1000L

class HomeRepositoryImpl @Inject constructor(
    private val api: FoodFactApi,
    private val dao: FoodFactDao,
    private val medicineReminderDao: MedicineReminderDao
) : HomeRepository {
    override suspend fun getFoodFact() : ResultState<FoodFactModel> {
        val apiKey = BuildConfig.SPOONACULAR_KEY

        return withContext(Dispatchers.IO) {
            try {
                val cachedData = dao.getFoodFact()
                val isCacheExpired =
                    cachedData == null || (System.currentTimeMillis() - cachedData.timestamp) >= TWENTY_FOUR_HOURS_IN_MILLIS

                if (isCacheExpired) {
                    val response = api.getFoodFact(apiKey)
                    val result = response.body()

                    if (response.isSuccessful && result != null) {
                        val entity = FoodFactEntity(
                            foodFact = result.foodFact,
                            timestamp = System.currentTimeMillis()
                        )
                        dao.addFoodFact(entity)

                        val latestFoodFact = dao.getFoodFact()
                        val model = FoodFactModel(
                            foodFact = latestFoodFact?.foodFact ?: "No fact available"
                        )
                        ResultState.Success(model)
                    } else {
                        val latestFoodFact = dao.getFoodFact()
                        val model = FoodFactModel(
                            foodFact = latestFoodFact?.foodFact ?: "No fact available"
                        )
                        ResultState.Success(model)
                    }
                } else {
                    ResultState.Success(FoodFactModel(foodFact = cachedData.foodFact))
                }
            } catch (_: Exception) {
                val cached = dao.getFoodFact()
                cached?.let {
                    ResultState.Success(FoodFactModel(foodFact = it.foodFact))
                } ?: ResultState.Error("Something went wrong")
            }
        }
    }

    override suspend fun addMedicineReminder(medicineReminderModel: MedicineReminderModel) {
        withContext(Dispatchers.IO) {
            medicineReminderDao.addMedicineReminder(
                MedicineReminderEntity(
                    id = 0, // 0 lets Room auto-generate the primary key
                    medicineName = medicineReminderModel.medicineName,
                    description = medicineReminderModel.description,
                    time = medicineReminderModel.time,
                    date = medicineReminderModel.date,
                    repeatDaily = medicineReminderModel.repeatDaily
                )
            )
        }
    }

    override suspend fun getAllMedicineReminders(): List<MedicineReminderModel> {
        return withContext(Dispatchers.IO) {
            medicineReminderDao.getAllMedicineReminders().map { entity ->
                MedicineReminderModel(
                    id = entity.id,
                    medicineName = entity.medicineName,
                    description = entity.description,
                    time = entity.time,
                    date = entity.date,
                    repeatDaily = entity.repeatDaily,
                    isTaken = entity.isTaken
                )
            }
        }
    }

    override suspend fun deleteMedicineReminder(reminderId: Int) {
        withContext(Dispatchers.IO) {
            medicineReminderDao.deleteMedicineReminder(reminderId)
        }
    }

    override suspend fun markReminderAsTaken(reminderId: Int) {
        withContext(Dispatchers.IO) {
            medicineReminderDao.markReminderAsTaken(reminderId)
        }
    }
}