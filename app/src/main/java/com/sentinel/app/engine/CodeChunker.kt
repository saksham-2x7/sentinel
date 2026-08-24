package com.sentinel.app.engine
object CodeChunker {
    private const val MAX_CHARS = 6000
    private const val OVERLAP_LINES = 5
    fun chunk(code: String): List<String> {
        if (code.length <= MAX_CHARS) return listOf(code)
        val lines = code.lines()
        val chunks = mutableListOf<String>()
        var startLine = 0
        while (startLine < lines.size) {
            val chunkLines = mutableListOf<String>()
            var charCount = 0
            var endLine = startLine
            while (endLine < lines.size && charCount + lines[endLine].length < MAX_CHARS) {
                chunkLines.add(lines[endLine])
                charCount += lines[endLine].length + 1
                endLine++
            }
            if (chunkLines.isEmpty()) { startLine++; continue }
            chunks.add(chunkLines.joinToString("\n"))
            startLine = maxOf(startLine + 1, endLine - OVERLAP_LINES)
        }
        return chunks.ifEmpty { listOf(code.take(MAX_CHARS)) }
    }
}
