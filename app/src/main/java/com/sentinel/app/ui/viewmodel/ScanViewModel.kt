package com.sentinel.app.ui.viewmodel
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.app.data.ScanRepository
import com.sentinel.app.domain.ScanResult
import com.sentinel.app.domain.ScanState
import com.sentinel.app.engine.LlmManager
import com.sentinel.app.engine.ScanEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("sentinel_prefs", Context.MODE_PRIVATE)
    private val repository = ScanRepository(application)
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()
    private val _modelPath = MutableStateFlow(prefs.getString("model_path", "") ?: "")
    val modelPath: StateFlow<String> = _modelPath.asStateFlow()
    private val _isModelReady = MutableStateFlow(_modelPath.value.isNotBlank())
    val isModelReady: StateFlow<Boolean> = _isModelReady.asStateFlow()
    private val _lastScanId = MutableStateFlow<Long?>(null)
    val lastScanId: StateFlow<Long?> = _lastScanId.asStateFlow()
    val scanHistory: Flow<List<ScanResult>> = repository.getAllScans()
    private var scanJob: Job? = null
    fun setModelPath(path: String) {
        prefs.edit().putString("model_path", path).apply()
        _modelPath.value = path
        _isModelReady.value = path.isNotBlank()
        LlmManager.reset()
    }
    fun scanCode(code: String, language: String, fileName: String = "Pasted code") {
        val path = _modelPath.value
        if (path.isBlank()) {
            _scanState.value = ScanState.Error("No model configured. Please set model path in Setup.")
            return
        }
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                _scanState.value = ScanState.Scanning(0f, "Initializing...")
                val result = ScanEngine.scan(
                    context = getApplication(),
                    modelPath = path,
                    code = code,
                    language = language,
                    fileName = fileName
                ) { progress, status ->
                    _scanState.value = ScanState.Scanning(progress, status)
                }
                val savedId = repository.saveScan(result)
                val savedResult = result.copy(id = savedId)
                _lastScanId.value = savedId
                _scanState.value = ScanState.Success(savedResult)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Scan failed. Check model path.")
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
