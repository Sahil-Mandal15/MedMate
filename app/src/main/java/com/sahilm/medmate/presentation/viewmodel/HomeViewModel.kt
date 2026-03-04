package com.sahilm.medmate.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilm.medmate.domain.model.FoodFactModel
import com.sahilm.medmate.domain.repository.HomeRepository
import com.sahilm.medmate.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {

    private val _foodFact : MutableLiveData<ResultState<FoodFactModel>> = MutableLiveData()
    val foodFact : LiveData<ResultState<FoodFactModel>> = _foodFact

    init {
        viewModelScope.launch {
            getFoodFact()
        }
    }

    suspend fun getFoodFact() {
        _foodFact.value = repository.getFoodFact()
        Log.d("TAG", "getFoodFact: ${foodFact}")
    }
}