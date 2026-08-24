package com.sentinel.app.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sentinel.app.data.ScanRepository
import com.sentinel.app.domain.ScanResult
import com.sentinel.app.domain.ScanState
import com.sentinel.app.engine.LlmManager
import com.sentinel.app.engine.ScanEngine
import com.sentinel.app.engine.CloudScanEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("sentinel_prefs", Context.MODE_PRIVATE)
    private val repository = ScanRepository(application)
    private val auth = FirebaseAuth.getInstance()
    
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()
    
    // Theme state
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    // Hardcode the default local model path
    private val defaultModelPath = "/data/data/com.sentinel.app/files/model.bin"
    
    private val _isModelReady = MutableStateFlow(auth.currentUser != null)
    val isModelReady: StateFlow<Boolean> = _isModelReady.asStateFlow()
    
    private val _lastScanId = MutableStateFlow<Long?>(null)
    val lastScanId: StateFlow<Long?> = _lastScanId.asStateFlow()
    
    val scanHistory: Flow<List<ScanResult>> = repository.getAllScans()
    private var scanJob: Job? = null
    
    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isModelReady.value = firebaseAuth.currentUser != null
        }
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
        _isDarkMode.value = enabled
    }
    
    fun setSetupComplete() {
        _isModelReady.value = true
        LlmManager.reset()
    }
    
    fun logout() {
        auth.signOut()
        _isModelReady.value = false
        LlmManager.reset()
    }

    fun scanCode(code: String, language: String, fileName: String = "Pasted code", useCloud: Boolean = false) {
        if (!useCloud && defaultModelPath.isBlank()) {
            _scanState.value = ScanState.Error("No offline model path configured.")
            return
        }
        
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                _scanState.value = ScanState.Scanning(0f, "Initializing...")
                val result = if (useCloud) {
                    CloudScanEngine.scan(code, language, fileName) { progress, status ->
                        _scanState.value = ScanState.Scanning(progress, status)
                    }
                } else {
                    ScanEngine.scan(getApplication(), defaultModelPath, code, language, fileName) { progress, status ->
                        _scanState.value = ScanState.Scanning(progress, status)
                    }
                }
                
                val savedId = repository.saveScan(result)
                val savedResult = result.copy(id = savedId)
                _lastScanId.value = savedId
                _scanState.value = ScanState.Success(savedResult)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Scan failed.")
            }
        }
    }
    
    fun cancelScan() {
        scanJob?.cancel()
        _scanState.value = ScanState.Idle
    }
    
    fun resetState() {
        _scanState.value = ScanState.Idle
        _lastScanId.value = null
    }
    
    suspend fun getScanById(id: Long): ScanResult? = repository.getScanById(id)
    
    fun deleteScan(id: Long) {
        viewModelScope.launch { repository.deleteScan(id) }
    }
    
    override fun onCleared() {
        super.onCleared()
        LlmManager.reset()
    }
}
