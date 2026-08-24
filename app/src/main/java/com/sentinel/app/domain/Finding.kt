package com.sentinel.app.domain
enum class FindingType {
    HARDCODED_SECRET,
    SQL_INJECTION,
    XSS,
    BROKEN_AUTH,
    INSECURE_COMMS,
    SENSITIVE_DATA,
    LOGIC_FLAW,
    DEPENDENCY_RISK;
    fun displayName(): String = when (this) {
        HARDCODED_SECRET -> "Hardcoded Secret"
        SQL_INJECTION -> "SQL Injection"
        XSS -> "Cross-Site Scripting"
        BROKEN_AUTH -> "Broken Authentication"
        INSECURE_COMMS -> "Insecure Communications"
        SENSITIVE_DATA -> "Sensitive Data Exposure"
        LOGIC_FLAW -> "Logic Flaw"
        DEPENDENCY_RISK -> "Dependency Risk"
    }
    fun emoji(): String = when (this) {
        HARDCODED_SECRET -> "🔑"
        SQL_INJECTION -> "💉"
        XSS -> "🌐"
        BROKEN_AUTH -> "🔓"
        INSECURE_COMMS -> "📡"
        SENSITIVE_DATA -> "📦"
        LOGIC_FLAW -> "⚠️"
        DEPENDENCY_RISK -> "📚"
    }
}
enum class Severity {
    HIGH, MEDIUM, LOW;
    fun label(): String = name.lowercase().replaceFirstChar { it.uppercase() }
}
data class Finding(
    val line: Int? = null,
    val type: FindingType,
    val severity: Severity,
    val title: String,
    val description: String,
    val fix: String
)
