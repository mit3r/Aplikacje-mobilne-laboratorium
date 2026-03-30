package com.example.pierwszaaplikacja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.model.TrailType
import com.example.pierwszaaplikacja.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrailListViewModel : ViewModel() {
    private val _trails = MutableStateFlow(emptyList<Trail>())
    val trails = _trails.asStateFlow()

    fun fetchTrails() {
        viewModelScope.launch {
            try {
                val list = RetrofitClient.apiService.getTrails()
                _trails.value = list
            } catch (e: Exception) {
//                e.printStackTrace()
            }

        }
    }
}