package com.example.trasy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trasy.model.Trail
import com.example.trasy.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TrailListViewModel : ViewModel() {
    private val _trails = MutableStateFlow(emptyList<Trail>())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val trails = combine(_trails, _searchQuery) { trails, query ->
        if (query.isBlank()) {
            trails
        } else {
            trails.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchTrails() {
        viewModelScope.launch {
            try {
                val list = RetrofitClient.apiService.getTrails()
                _trails.value = list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}