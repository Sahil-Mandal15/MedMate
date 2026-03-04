package com.sahilm.medmate.data.dto

import com.google.gson.annotations.SerializedName

data class FoodFactResponse(
    @SerializedName("text")
    val foodFact: String
)
