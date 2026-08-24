package com.sentinel.app.engine
import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class LlmManager private constructor(private val llm: LlmInference) {
    suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        llm.generateResponse(prompt)
    }
    fun close() {
        try { llm.close() } catch (_: Exception) {}
    }
    companion object {
        @Volatile private var instance: LlmManager? = null
        private var currentModelPath: String = ""
        suspend fun getInstance(context: Context, modelPath: String): LlmManager {
            return withContext(Dispatchers.IO) {
                if (instance == null || currentModelPath != modelPath) {
                    instance?.close()
                    val options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelPath)
                        .setMaxTokens(1024)
                        .setTopK(40)
                        .setTemperature(0.1f)
                        .setRandomSeed(42)
                        .build()
                    instance = LlmManager(LlmInference.createFromOptions(context, options))
                    currentModelPath = modelPath
                }
                instance!!
            }
        }
        fun reset() {
            instance?.close()
            instance = null
            currentModelPath = ""
        }
    }
}
