package com.disciplineos.app.domain

data class DailyEntry(
    val date: String,
    val sleepHours: Float = 0f,
    val proteinGrams: Float = 0f,
    val waterLiters: Float = 0f,
    val disciplineCheck: Boolean = false,
    val score: Int = 0,
)

data class ScoreBreakdown(
    val total: Int,
    val workout: Int,
    val sleep: Int,
    val protein: Int,
    val water: Int,
    val discipline: Int,
)

data class DashboardStats(
    val weeklyScores: List<DayScore>,
    val averageScore: Float,
    val streak: Int,
)

data class DayScore(
    val date: String,
    val label: String,
    val score: Int,
)

data class WorkoutPlan(
    val id: Long = 0,
    val name: String,
    val isRest: Boolean = false,
    val exercises: List<PlanExercise> = emptyList(),
)

data class PlanExercise(
    val id: Long = 0,
    val planId: Long = 0,
    val name: String,
    val detail: String = "",
    val sortOrder: Int = 0,
)

data class ChecklistItem(
    val exerciseId: Long,
    val name: String,
    val detail: String,
    val completed: Boolean,
)

data class TodayWorkout(
    val plan: WorkoutPlan?,
    val items: List<ChecklistItem>,
    val completedCount: Int,
    val totalCount: Int,
    val workoutPoints: Int,
    val isRestDay: Boolean,
)

data class WeekSchedule(
    /** weekday 1=Mon … 7=Sun → plan id */
    val assignments: Map<Int, Long>,
)
