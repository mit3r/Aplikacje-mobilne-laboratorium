package com.example.pierwszaaplikacja.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pierwszaaplikacja.model.AppDatabase
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.model.TripLog
import com.example.pierwszaaplikacja.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrailDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val tripLogDao = db.tripLogDao()

    private val _trail = MutableStateFlow<Trail?>(null)
    val trail = _trail.asStateFlow()

    private val _currentTrailId = MutableStateFlow<Int?>(null)
    
    val tripLogs: StateFlow<List<TripLog>> = _currentTrailId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else tripLogDao.getLogsForTrail(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchTrail(trailId: Int) {
        _currentTrailId.value = trailId
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

    fun saveTripLog(timeInSeconds: Long) {
        val trailId = _currentTrailId.value ?: return
        viewModelScope.launch {
            tripLogDao.insert(TripLog(trailId = trailId, timeInSeconds = timeInSeconds))
        }
    }

    fun deleteTripLog(tripLog: TripLog) {
        viewModelScope.launch {
            tripLogDao.delete(tripLog)
        }
    }
}
