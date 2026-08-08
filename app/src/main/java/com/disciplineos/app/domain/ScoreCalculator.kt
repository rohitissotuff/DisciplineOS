package com.disciplineos.app.domain

object ScoreCalculator {
    const val WORKOUT_MAX = 30
    const val SLEEP_MAX = 20
    const val PROTEIN_MAX = 20
    const val WATER_MAX = 10
    const val DISCIPLINE_MAX = 20

    const val SLEEP_TARGET_HOURS = 8f
    const val PROTEIN_TARGET_GRAMS = 150f
    const val WATER_TARGET_LITERS = 3f

    const val STREAK_THRESHOLD = 70

    fun workoutPoints(isRestDay: Boolean, completed: Int, total: Int): Int {
        if (isRestDay) return WORKOUT_MAX
        if (total <= 0) return 0
        return ((completed.toFloat() / total.toFloat()) * WORKOUT_MAX).toInt()
            .coerceIn(0, WORKOUT_MAX)
    }

    fun breakdown(
        workoutPoints: Int,
        sleepHours: Float,
        proteinGrams: Float,
        waterLiters: Float,
        disciplineCheck: Boolean,
    ): ScoreBreakdown {
        val workout = workoutPoints.coerceIn(0, WORKOUT_MAX)
        val sleep = scaledPoints(sleepHours, SLEEP_TARGET_HOURS, SLEEP_MAX)
        val protein = scaledPoints(proteinGrams, PROTEIN_TARGET_GRAMS, PROTEIN_MAX)
        val water = scaledPoints(waterLiters, WATER_TARGET_LITERS, WATER_MAX)
        val discipline = if (disciplineCheck) DISCIPLINE_MAX else 0
        return ScoreBreakdown(
            total = workout + sleep + protein + water + discipline,
            workout = workout,
            sleep = sleep,
            protein = protein,
            water = water,
            discipline = discipline,
        )
    }

    fun calculate(
        workoutPoints: Int,
        sleepHours: Float,
        proteinGrams: Float,
        waterLiters: Float,
        disciplineCheck: Boolean,
    ): Int = breakdown(
        workoutPoints,
        sleepHours,
        proteinGrams,
        waterLiters,
        disciplineCheck,
    ).total

    private fun scaledPoints(value: Float, target: Float, maxPoints: Int): Int {
        if (value <= 0f || target <= 0f) return 0
        val ratio = (value / target).coerceIn(0f, 1f)
        return (ratio * maxPoints).toInt()
    }
}
