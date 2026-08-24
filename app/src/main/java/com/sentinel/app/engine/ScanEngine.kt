package com.sentinel.app.engine
import android.content.Context
import com.sentinel.app.domain.ScanResult
object ScanEngine {
    suspend fun scan(
        context: Context,
        modelPath: String,
        code: String,
        language: String,
        fileName: String,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): ScanResult {
        val startTime = System.currentTimeMillis()
        onProgress(0.1f, "Loading model...")
        val llm = LlmManager.getInstance(context, modelPath)
        onProgress(0.3f, "Chunking code...")
        val chunks = CodeChunker.chunk(code)
        onProgress(0.4f, "Analyzing code...")
        val codePreview = code.take(150)
        val primaryChunk = chunks.first()
        val prompt = PromptBuilder.buildScanPrompt(primaryChunk, language)
        onProgress(0.5f, "Running on-device AI...")
        val rawResponse = llm.generate(prompt)
        onProgress(0.9f, "Parsing results...")
        val duration = System.currentTimeMillis() - startTime
        return ResultParser.parse(rawResponse, language, codePreview, fileName, duration)
    }
}
