package com.sentinel.app.domain
sealed class ScanState {
    object Idle : ScanState()
    object ModelLoading : ScanState()
    data class Scanning(
        val progress: Float = 0f,
        val statusText: String = "Analyzing..."
    ) : ScanState()
    data class Success(val result: ScanResult) : ScanState()
    data class Error(val message: String) : ScanState()
}
