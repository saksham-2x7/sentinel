package com.sentinel.app.engine

import com.google.ai.client.generativeai.GenerativeModel
import com.sentinel.app.domain.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object CloudScanEngine {
    private const val GEMINI_API_KEY = "AQ.Ab8RN6LSvrzkbnANtVMo5EgFi9cHjcU1nyXVmwPL-jV0T8O0wg"

    suspend fun scan(
        code: String,
        language: String,
        fileName: String,
        onProgress: (Float, String) -> Unit
    ): ScanResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        onProgress(0.1f, "Connecting to Cloud Agent...")
        
        // Try the stable 3.6-pro model to bypass free-tier Flash congestion
        val generativeModel = GenerativeModel(
            modelName = "gemini-3.6-pro",
            apiKey = GEMINI_API_KEY
        )
        
        onProgress(0.4f, "Analyzing code in cloud...")
        val prompt = PromptBuilder.buildScanPrompt(code, language)
        
        var responseText = ""
        var attempts = 0
        val maxAttempts = 3
        
        while (attempts < maxAttempts) {
            try {
                val response = generativeModel.generateContent(prompt)
                responseText = response.text ?: ""
                break
            } catch (e: Exception) {
                attempts++
                if (attempts >= maxAttempts || (!e.message.toString().contains("503") && !e.message.toString().contains("UNAVAILABLE"))) {
                    throw Exception("Cloud Agent failed after retries: ${e.message}")
                }
                onProgress(0.5f, "Server crowded, silently retrying ($attempts/3)...")
                delay(2000)
            }
        }
        
        onProgress(0.8f, "Parsing cloud results...")
        val codePreview = code.take(150)
        val duration = System.currentTimeMillis() - startTime
        ResultParser.parse(responseText, language, codePreview, fileName, duration)
    }
}
