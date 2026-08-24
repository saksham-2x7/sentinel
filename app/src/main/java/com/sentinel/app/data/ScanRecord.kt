package com.sentinel.app.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "scan_records")
data class ScanRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long,
    val language: String,
    val codePreview: String,
    val fileName: String,
    val securityScore: Int,
    val findingsJson: String,
    val scanDurationMs: Long,
    val totalFindings: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int
)
