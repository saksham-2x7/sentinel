package com.sentinel.app.engine
object PromptBuilder {
    fun buildScanPrompt(code: String, language: String): String = """
<|system|>
You are an expert security code analyzer. Analyze the provided code for security vulnerabilities.
Return ONLY a valid JSON object — no markdown, no explanation, no extra text. Just the JSON.

Return exactly this structure:
{"security_score":<integer 0-100 where 100=perfectly secure>,"findings":[{"line":<integer or null>,"type":"<one of: HARDCODED_SECRET|SQL_INJECTION|XSS|BROKEN_AUTH|INSECURE_COMMS|SENSITIVE_DATA|LOGIC_FLAW|DEPENDENCY_RISK>","severity":"<HIGH|MEDIUM|LOW>","title":"<max 8 words>","description":"<EXACT vulnerable code snippet>","fix":"<EXACT corrected code snippet>"}]}

Rules:
- Only report real vulnerabilities, not style issues
- Estimate line numbers when identifiable
- If no vulnerabilities: {"security_score":95,"findings":[]}
- NEVER include markdown code fences in your response
- ONLY output the JSON object, starting with { and ending with }
<|end|>
<|user|>
Analyze this $language code for security vulnerabilities:

${code.take(6000)}
<|end|>
<|assistant|>
{"security_score":""".trimIndent()
    fun buildContinuationPrefix(): String = """{"security_score":"""
}
