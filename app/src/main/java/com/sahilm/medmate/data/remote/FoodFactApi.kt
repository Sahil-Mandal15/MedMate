package com.sahilm.medmate.data.remote

import com.sahilm.medmate.data.dto.FoodFactResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FoodFactApi {
    @GET("food/trivia/random")
    suspend fun getFoodFact(
        @Header("x-api-key") apiKey: String
    ) : Response<FoodFactResponse>
}