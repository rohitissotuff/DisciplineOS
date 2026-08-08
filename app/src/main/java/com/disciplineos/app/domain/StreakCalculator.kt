package com.disciplineos.app.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {
    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): String = LocalDate.now().format(isoFormatter)

    fun format(date: LocalDate): String = date.format(isoFormatter)

    fun parse(date: String): LocalDate = LocalDate.parse(date, isoFormatter)

    fun dayLabel(date: String): String {
        val local = parse(date)
        return local.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .uppercase(Locale.getDefault())
    }

    fun lastSevenDays(endingOn: LocalDate = LocalDate.now()): List<LocalDate> {
        return (6 downTo 0).map { endingOn.minusDays(it.toLong()) }
    }
}

object StreakCalculator {
    /**
     * Counts consecutive days with score >= threshold, walking backward from [fromDate].
     * Missing days break the streak.
     */
    fun calculate(
        entriesByDate: Map<String, Int>,
        fromDate: LocalDate = LocalDate.now(),
        threshold: Int = ScoreCalculator.STREAK_THRESHOLD,
    ): Int {
        var streak = 0
        var cursor = fromDate
        while (true) {
            val key = DateUtils.format(cursor)
            val score = entriesByDate[key] ?: break
            if (score < threshold) break
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
