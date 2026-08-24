package com.sentinel.app.data
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sentinel.app.domain.Finding
import com.sentinel.app.domain.ScanResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class ScanRepository(context: Context) {
    private val dao = ScanDatabase.getInstance(context).scanDao()
    private val gson = Gson()
    fun getAllScans(): Flow<List<ScanResult>> = dao.getAllScans().map { records ->
        records.map { it.toScanResult() }
    }
    suspend fun getScanById(id: Long): ScanResult? = dao.getScanById(id)?.toScanResult()
    suspend fun saveScan(result: ScanResult): Long {
        val record = ScanRecord(
            timestamp = result.timestamp,
            language = result.language,
            codePreview = result.codePreview,
            fileName = result.fileName,
            securityScore = result.securityScore,
            findingsJson = gson.toJson(result.findings),
            scanDurationMs = result.scanDurationMs,
            totalFindings = result.totalFindings,
            highCount = result.highCount,
            mediumCount = result.mediumCount,
            lowCount = result.lowCount
        )
        return dao.insertScan(record)
    }
    suspend fun deleteScan(id: Long) = dao.deleteScan(id)
    private fun ScanRecord.toScanResult(): ScanResult {
        val type = object : TypeToken<List<Finding>>() {}.type
        val findings: List<Finding> = try {
            gson.fromJson(findingsJson, type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
        return ScanResult(
            id = id,
            timestamp = timestamp,
            language = language,
            codePreview = codePreview,
            fileName = fileName,
            securityScore = securityScore,
            findings = findings,
            scanDurationMs = scanDurationMs
        )
    }
}
