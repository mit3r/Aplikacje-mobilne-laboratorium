package com.example.pierwszaaplikacja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrailDetailsViewModel : ViewModel() {
    private val _trail = MutableStateFlow<Trail?>(null)
    val trail = _trail.asStateFlow()

    fun fetchTrail(trailId: Int) {
        viewModelScope.launch {
            _trail.value = null
            try {
                val item = RetrofitClient.apiService.getTrail(trailId)
                _trail.value = item

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}