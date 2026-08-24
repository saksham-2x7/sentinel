package com.sentinel.app.engine
import com.google.gson.Gson
import com.sentinel.app.domain.Finding
import com.sentinel.app.domain.FindingType
import com.sentinel.app.domain.ScanResult
import com.sentinel.app.domain.Severity
object ResultParser {
    private val gson = Gson()
    data class LlmFinding(
        val line: Int? = null,
        val type: String = "",
        val severity: String = "",
        val title: String = "",
        val description: String = "",
        val fix: String = ""
    )
    data class LlmResponse(
        val security_score: Int = 50,
        val findings: List<LlmFinding> = emptyList()
    )
    fun parse(
        rawResponse: String,
        language: String,
        codePreview: String,
        fileName: String,
        durationMs: Long
    ): ScanResult {
        val json = extractJson(rawResponse)
        return try {
            val response = gson.fromJson(json, LlmResponse::class.java)
            ScanResult(
                language = language,
                codePreview = codePreview,
                fileName = fileName,
                securityScore = response.security_score.coerceIn(0, 100),
                findings = response.findings.mapNotNull { it.toFinding() },
                scanDurationMs = durationMs
            )
        } catch (e: Exception) {
            ScanResult(
                language = language,
                codePreview = codePreview,
                fileName = fileName,
                securityScore = 50,
                findings = emptyList(),
                scanDurationMs = durationMs
            )
        }
    }
    private fun extractJson(raw: String): String {
        val cleaned = raw.trim()
        val withPrefix = if (cleaned.startsWith("{")) cleaned else "{\"security_score\":$cleaned"
        val lastBrace = withPrefix.lastIndexOf('}')
        return if (lastBrace >= 0) withPrefix.substring(0, lastBrace + 1) else withPrefix
    }
    private fun LlmFinding.toFinding(): Finding? {
        val findingType = try { FindingType.valueOf(type.trim().uppercase()) } catch (e: Exception) { return null }
        val severityEnum = try { Severity.valueOf(severity.trim().uppercase()) } catch (e: Exception) { Severity.MEDIUM }
        if (title.isBlank()) return null
        return Finding(
            line = line,
            type = findingType,
            severity = severityEnum,
            title = title.take(80),
            description = description.take(300),
            fix = fix.take(300)
        )
    }
}
