package com.sentinel.app.domain
data class ScanResult(
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String,
    val codePreview: String,
    val fileName: String = "Pasted code",
    val securityScore: Int,
    val findings: List<Finding>,
    val scanDurationMs: Long
) {
    val highCount get() = findings.count { it.severity == Severity.HIGH }
    val mediumCount get() = findings.count { it.severity == Severity.MEDIUM }
    val lowCount get() = findings.count { it.severity == Severity.LOW }
    val totalFindings get() = findings.size
    val isSecure get() = findings.isEmpty()
}
