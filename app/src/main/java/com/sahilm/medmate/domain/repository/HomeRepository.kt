package com.sahilm.medmate.domain.repository

import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.utils.ResultState

interface HomeRepository {
    suspend fun getFoodFact() : ResultState<FoodFactModel>
}