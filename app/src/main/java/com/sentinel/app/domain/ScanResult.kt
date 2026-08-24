package com.sentinel.app.domain

import com.google.firebase.firestore.Exclude

data class ScanResult(
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "",
    val codePreview: String = "",
    val fileName: String = "Pasted code",
    val securityScore: Int = 100,
    val findings: List<Finding> = emptyList(),
    val scanDurationMs: Long = 0L
) {
    @get:Exclude val highCount get() = findings.count { it.severity == Severity.HIGH }
    @get:Exclude val mediumCount get() = findings.count { it.severity == Severity.MEDIUM }
    @get:Exclude val lowCount get() = findings.count { it.severity == Severity.LOW }
    @get:Exclude val totalFindings get() = findings.size
    @get:Exclude val isSecure get() = findings.isEmpty()
}
