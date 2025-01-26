package com.sahilm.medmate.api

import com.sahilm.medmate.BuildConfig
import com.sahilm.medmate.model.FoodFactResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DailyFoodFactAPi {

    @GET("food/trivia/random")
    suspend fun getRandomFoodFact(
        @Query("apiKey") apiKey: String = BuildConfig.SPOONACULAR_API_KEY
    ): Response<FoodFactResponse>
}