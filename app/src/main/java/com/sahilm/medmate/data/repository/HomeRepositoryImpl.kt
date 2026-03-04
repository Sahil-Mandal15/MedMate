package com.sahilm.medmate.data.repository

import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.data.local.dao.FoodFactDao
import com.sahilm.medmate.data.local.entity.FoodFactEntity
import com.sahilm.medmate.data.remote.FoodFactApi
import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.domain.repository.HomeRepository
import com.sahilm.medmate.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TWENTY_FOUR_HOURS_IN_MILLIS = 24 * 60 * 60 * 1000L

class HomeRepositoryImpl @Inject constructor(
    private val api: FoodFactApi,
    private val dao: FoodFactDao
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
            } catch (e: Exception) {
                val cached = dao.getFoodFact()
                cached?.let {
                    ResultState.Success(FoodFactModel(foodFact = it.foodFact))
                } ?: ResultState.Error("Something went wrong")
            }
        }
    }
}