package com.disciplineos.app.domain

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Emits the current calendar date and re-emits at local midnight
 * (and periodically as a safety net for sleep/timezone shifts).
 */
object DayClock {
    fun observeDateKeys(): Flow<String> = flow {
        while (currentCoroutineContext().isActive) {
            emit(DateUtils.today())
            delay(millisUntilNextBoundary())
        }
    }

    private fun millisUntilNextBoundary(): Long {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val untilMidnight = Duration.between(now, nextMidnight).toMillis()
        // Wake at midnight, but also check at least every 15 minutes
        // in case the device slept through the exact boundary.
        return minOf(untilMidnight.coerceAtLeast(1_000L), 15 * 60 * 1_000L)
    }
}

object DateDisplay {
    private val headerFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE · MMM d", Locale.getDefault())

    fun headerLabel(date: String = DateUtils.today()): String {
        return DateUtils.parse(date).format(headerFormatter).uppercase(Locale.getDefault())
    }

    fun headerLabel(date: LocalDate): String =
        date.format(headerFormatter).uppercase(Locale.getDefault())
}
