package com.sahilm.medmate.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sahilm.medmate.model.FoodFactDetails
import com.sahilm.medmate.model.FoodFactResponse
import com.sahilm.medmate.repository.MedmateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.util.Calendar
import java.util.Date

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("imp_items")

class MedMateViewModel(
    private val medmateRepository: MedmateRepository,
    private val context: Context
) {

    private lateinit var calendar: Calendar

    companion object {
        val FOOD_FACT = stringPreferencesKey("food_fact")
        val FACT_TIME = stringPreferencesKey("fact_time")
    }

    suspend fun saveFactDetails(
        foodFact: String = "",
        factTime: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[FOOD_FACT] = foodFact
            preferences[FACT_TIME] = factTime
        }
    }

    val foodFactDetails: Flow<FoodFactDetails> = context.dataStore.data.map { preferences ->
        FoodFactDetails(
            foodFact = preferences[FOOD_FACT] ?: "",
            factTime = preferences[FACT_TIME] ?: ""
        )
    }

    suspend fun getFoodFact(apiKey: String){
        calendar = Calendar.getInstance()
       medmateRepository.getFoodFact(apiKey).let { details ->
           saveFactDetails(
               foodFact = details.body()!!.text.toString(),
               factTime = calendar.time.toString().substring(4, 11)
           )
       }
    }
}