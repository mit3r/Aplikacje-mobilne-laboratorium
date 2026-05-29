package com.example.pierwszaaplikacja.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pierwszaaplikacja.model.AppDatabase
import com.example.pierwszaaplikacja.model.Trail
import com.example.pierwszaaplikacja.model.TripLog
import com.example.pierwszaaplikacja.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TrailDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val tripLogDao = db.tripLogDao()

    private val _trails = MutableStateFlow<List<Trail>>(emptyList())
    val trails = _trails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentTrailId = MutableStateFlow<Int?>(null)
    val currentTrailId = _currentTrailId.asStateFlow()

    val tripLogs: StateFlow<List<TripLog>> = _currentTrailId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else tripLogDao.getLogsForTrail(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadAllTrails(initialTrailId: Int) {
        _currentTrailId.value = initialTrailId
        if (_trails.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = RetrofitClient.apiService.getTrails()
                _trails.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCurrentTrailId(id: Int) {
        _currentTrailId.value = id
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
