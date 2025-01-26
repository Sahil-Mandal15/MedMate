package com.sahilm.medmate.repository

import com.sahilm.medmate.api.RetrofitInstance

class MedmateRepository {

    suspend fun getFoodFact(apiKey: String) =
        RetrofitInstance.api.getRandomFoodFact(apiKey)


}