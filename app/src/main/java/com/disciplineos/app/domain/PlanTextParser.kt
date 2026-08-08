package com.disciplineos.app.domain

/**
 * Parses pasted workout plans.
 * Supports lines like:
 *   Bench Press 4x8
 *   Incline DB Press - 3x10
 *   Cable Fly
 * Skips blank lines and lines starting with #.
 */
object PlanTextParser {
    private val detailSuffix = Regex(
        """^(.*?)(?:\s*[-–—:]\s*|\s+)(\d+\s*[x×]\s*\d+[a-zA-Z0-9\s/-]*)$"""
    )

    fun parse(text: String): List<ParsedExercise> {
        return text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .mapIndexed { index, line ->
                val match = detailSuffix.find(line)
                if (match != null) {
                    val name = match.groupValues[1].trim()
                    val detail = match.groupValues[2].trim()
                    ParsedExercise(
                        name = name.ifBlank { line },
                        detail = if (name.isBlank()) "" else detail,
                        sortOrder = index,
                    )
                } else {
                    ParsedExercise(name = line, detail = "", sortOrder = index)
                }
            }
            .filter { it.name.isNotBlank() }
            .toList()
    }

    data class ParsedExercise(
        val name: String,
        val detail: String,
        val sortOrder: Int,
    )
}
