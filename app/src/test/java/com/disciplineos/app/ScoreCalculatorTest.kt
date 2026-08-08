package com.disciplineos.app

import com.disciplineos.app.domain.FeedbackEngine
import com.disciplineos.app.domain.PlanTextParser
import com.disciplineos.app.domain.ScoreCalculator
import com.disciplineos.app.domain.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ScoreCalculatorTest {

    @Test
    fun fullDayScores100() {
        val score = ScoreCalculator.calculate(
            workoutPoints = 30,
            sleepHours = 8f,
            proteinGrams = 150f,
            waterLiters = 3f,
            disciplineCheck = true,
        )
        assertEquals(100, score)
    }

    @Test
    fun emptyDayScoresZero() {
        val score = ScoreCalculator.calculate(
            workoutPoints = 0,
            sleepHours = 0f,
            proteinGrams = 0f,
            waterLiters = 0f,
            disciplineCheck = false,
        )
        assertEquals(0, score)
    }

    @Test
    fun restDayGetsFullWorkoutPoints() {
        assertEquals(30, ScoreCalculator.workoutPoints(isRestDay = true, completed = 0, total = 0))
    }

    @Test
    fun checklistPartialScalesWorkoutPoints() {
        assertEquals(15, ScoreCalculator.workoutPoints(isRestDay = false, completed = 2, total = 4))
    }

    @Test
    fun partialSleepScales() {
        val breakdown = ScoreCalculator.breakdown(
            workoutPoints = 0,
            sleepHours = 4f,
            proteinGrams = 0f,
            waterLiters = 0f,
            disciplineCheck = false,
        )
        assertEquals(10, breakdown.sleep)
    }

    @Test
    fun feedbackIsStrictAtLowScores() {
        assertEquals("You failed the day. Own it and reset tomorrow.", FeedbackEngine.messageFor(10))
    }

    @Test
    fun streakBreaksBelowThreshold() {
        val today = LocalDate.of(2026, 8, 8)
        val scores = mapOf(
            "2026-08-08" to 80,
            "2026-08-07" to 75,
            "2026-08-06" to 60,
            "2026-08-05" to 90,
        )
        assertEquals(2, StreakCalculator.calculate(scores, today))
    }

    @Test
    fun parsesPlainTextPlan() {
        val parsed = PlanTextParser.parse(
            """
            Bench Press 4x8
            Incline DB Press - 3x10
            Cable Fly
            # comment ignored
            
            Tricep Pushdown 3x12
            """.trimIndent(),
        )
        assertEquals(4, parsed.size)
        assertEquals("Bench Press", parsed[0].name)
        assertEquals("4x8", parsed[0].detail)
        assertEquals("Incline DB Press", parsed[1].name)
        assertEquals("3x10", parsed[1].detail)
        assertEquals("Cable Fly", parsed[2].name)
        assertTrue(parsed[2].detail.isEmpty())
    }
}
