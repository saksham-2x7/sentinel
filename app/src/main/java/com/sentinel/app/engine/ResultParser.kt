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
            // If the model messed up the JSON, let's at least show a generic finding so the UI isn't empty!
            ScanResult(
                language = language,
                codePreview = codePreview,
                fileName = fileName,
                securityScore = 40,
                findings = listOf(
                    Finding(
                        line = null,
                        type = FindingType.LOGIC_FLAW,
                        severity = Severity.HIGH,
                        title = "Unformatted AI Response",
                        description = "The AI found issues but failed to format them into valid JSON. Raw output snippet: ${rawResponse.take(100)}...",
                        fix = "Try scanning a smaller snippet of code or rewording the prompt."
                    )
                ),
                scanDurationMs = durationMs
            )
        }
    }

    private fun extractJson(raw: String): String {
        var cleaned = raw.trim()
        // Strip markdown code fences if the model ignored the instructions
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json").trim()
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```").trim()
        }
        
        val withPrefix = if (cleaned.startsWith("{")) cleaned else "{\"security_score\":$cleaned"
        val lastBrace = withPrefix.lastIndexOf('}')
        return if (lastBrace >= 0) withPrefix.substring(0, lastBrace + 1) else withPrefix
    }

    private fun LlmFinding.toFinding(): Finding? {
        // Relaxed enum matching! Default to LOGIC_FLAW if the AI hallucinated a type.
        val findingType = try { 
            FindingType.valueOf(type.trim().uppercase().replace(" ", "_")) 
        } catch (e: Exception) { 
            FindingType.LOGIC_FLAW 
        }
        
        val severityEnum = try { 
            Severity.valueOf(severity.trim().uppercase()) 
        } catch (e: Exception) { 
            Severity.MEDIUM 
        }
        
        return Finding(
            line = line,
            type = findingType,
            severity = severityEnum,
            title = if (title.isNotBlank()) title.take(80) else "Security Risk",
            description = if (description.isNotBlank()) description.take(300) else "An issue was detected here.",
            fix = if (fix.isNotBlank()) fix.take(300) else "Review this code block."
        )
    }
}
