package com.sentinel.app.engine

import com.google.ai.client.generativeai.GenerativeModel
import com.sentinel.app.domain.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudScanEngine {
    // Secretly hardcoded API key for seamless free tier access
    private const val GEMINI_API_KEY = "AQ.Ab8RN6K1oj45YHCvOchY3SEaz3W7Ql-NW10TKaMPCXt7wVH3SA"

    suspend fun scan(
        code: String,
        language: String,
        fileName: String,
        onProgress: (Float, String) -> Unit
    ): ScanResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        onProgress(0.1f, "Connecting to Cloud Agent...")
        
        val generativeModel = GenerativeModel(
            modelName = "gemini-3.6-flash",
            apiKey = GEMINI_API_KEY
        )
        
        onProgress(0.4f, "Analyzing code in cloud...")
        val prompt = PromptBuilder.buildScanPrompt(code, language)
        
        val response = generativeModel.generateContent(prompt)
        val responseText = response.text ?: ""
        
        onProgress(0.8f, "Parsing cloud results...")
        val codePreview = code.take(150)
        val duration = System.currentTimeMillis() - startTime
        ResultParser.parse(responseText, language, codePreview, fileName, duration)
    }
}
