package com.disciplineos.app.data.repository

import com.disciplineos.app.data.database.CheckoffDao
import com.disciplineos.app.data.database.CheckoffEntity
import com.disciplineos.app.data.database.DailyEntryDao
import com.disciplineos.app.data.database.DailyEntryEntity
import com.disciplineos.app.data.database.ExerciseDao
import com.disciplineos.app.data.database.ExerciseEntity
import com.disciplineos.app.data.database.PlanDao
import com.disciplineos.app.data.database.PlanEntity
import com.disciplineos.app.data.database.ScheduleDao
import com.disciplineos.app.data.database.ScheduleEntity
import com.disciplineos.app.domain.ChecklistItem
import com.disciplineos.app.domain.DailyEntry
import com.disciplineos.app.domain.DashboardStats
import com.disciplineos.app.domain.DateUtils
import com.disciplineos.app.domain.DayScore
import com.disciplineos.app.domain.PlanExercise
import com.disciplineos.app.domain.PlanTextParser
import com.disciplineos.app.domain.ScoreCalculator
import com.disciplineos.app.domain.StreakCalculator
import com.disciplineos.app.domain.TodayWorkout
import com.disciplineos.app.domain.WorkoutPlan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class TodaySnapshot(
    val entry: DailyEntry,
    val workout: TodayWorkout,
)

@Singleton
class DisciplineRepository @Inject constructor(
    private val dailyDao: DailyEntryDao,
    private val planDao: PlanDao,
    private val exerciseDao: ExerciseDao,
    private val scheduleDao: ScheduleDao,
    private val checkoffDao: CheckoffDao,
) {
    fun observePlans(): Flow<List<WorkoutPlan>> {
        return planDao.observeAll().map { plans ->
            plans.map { plan ->
                WorkoutPlan(
                    id = plan.id,
                    name = plan.name,
                    isRest = plan.isRest,
                )
            }
        }
    }

    fun observePlan(planId: Long): Flow<WorkoutPlan?> {
        return combine(
            planDao.observeById(planId),
            exerciseDao.observeForPlan(planId),
        ) { plan, exercises ->
            plan?.toDomain(exercises)
        }
    }

    fun observeSchedule(): Flow<Map<Int, Long>> {
        return scheduleDao.observeAll().map { rows ->
            rows.associate { it.weekday to it.planId }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeToday(date: String = DateUtils.today()): Flow<TodaySnapshot> {
        val localDate = DateUtils.parse(date)
        return combine(
            dailyDao.observeByDate(date),
            planDao.observeAll(),
            scheduleDao.observeAll(),
            checkoffDao.observeForDate(date),
        ) { entry, plans, schedule, checkoffs ->
            TodayInputs(entry, plans, schedule, checkoffs)
        }.flatMapLatest { inputs ->
            val planEntity = resolvePlanEntity(localDate, inputs.plans, inputs.schedule)
            when {
                planEntity == null -> flowOf(
                    buildSnapshot(date, inputs.entry, null, emptyList(), inputs.checkoffs),
                )
                planEntity.isRest -> flowOf(
                    buildSnapshot(
                        date,
                        inputs.entry,
                        planEntity.toDomain(emptyList()),
                        emptyList(),
                        inputs.checkoffs,
                    ),
                )
                else -> exerciseDao.observeForPlan(planEntity.id).map { exercises ->
                    buildSnapshot(
                        date,
                        inputs.entry,
                        planEntity.toDomain(exercises),
                        exercises,
                        inputs.checkoffs,
                    )
                }
            }
        }
    }

    fun observeDashboard(endingOn: LocalDate = LocalDate.now()): Flow<DashboardStats> {
        val days = DateUtils.lastSevenDays(endingOn)
        return dailyDao.observeAll().map { allEntries ->
            val byDate = allEntries.associate { it.date to it.score }
            val weekly = days.map { day ->
                val key = DateUtils.format(day)
                DayScore(
                    date = key,
                    label = DateUtils.dayLabel(key),
                    score = byDate[key] ?: 0,
                )
            }
            val loggedThisWeek = weekly.filter { byDate.containsKey(it.date) }
            val average = if (loggedThisWeek.isEmpty()) {
                0f
            } else {
                loggedThisWeek.map { it.score }.average().toFloat()
            }
            DashboardStats(
                weeklyScores = weekly,
                averageScore = average,
                streak = StreakCalculator.calculate(byDate, endingOn),
            )
        }
    }

    suspend fun updateHabits(
        sleepHours: Float? = null,
        proteinGrams: Float? = null,
        waterLiters: Float? = null,
        disciplineCheck: Boolean? = null,
        date: String = DateUtils.today(),
    ) {
        recalcAndSave(date) { current ->
            current.copy(
                sleepHours = sleepHours ?: current.sleepHours,
                proteinGrams = proteinGrams ?: current.proteinGrams,
                waterLiters = waterLiters ?: current.waterLiters,
                disciplineCheck = disciplineCheck ?: current.disciplineCheck,
            )
        }
    }

    suspend fun setExerciseCompleted(
        exerciseId: Long,
        completed: Boolean,
        date: String = DateUtils.today(),
    ) {
        checkoffDao.setCompleted(date, exerciseId, completed)
        recalcAndSave(date) { it }
    }

    suspend fun createPlan(
        name: String,
        isRest: Boolean = false,
        assignToAllWeekdays: Boolean = false,
    ): Long {
        val id = planDao.upsert(PlanEntity(name = name.trim(), isRest = isRest))
        if (assignToAllWeekdays) {
            scheduleDao.upsertAll((1..7).map { ScheduleEntity(weekday = it, planId = id) })
        }
        return id
    }

    suspend fun renamePlan(planId: Long, name: String) {
        val plan = planDao.getById(planId) ?: return
        planDao.update(plan.copy(name = name.trim()))
    }

    suspend fun deletePlan(planId: Long) {
        planDao.delete(planId)
        scheduleDao.getAll()
            .filter { it.planId == planId }
            .forEach { scheduleDao.clearWeekday(it.weekday) }
    }

    suspend fun ensureRestPlan(): Long {
        val existing = planDao.getAll().firstOrNull { it.isRest }
        if (existing != null) return existing.id
        return planDao.upsert(PlanEntity(name = "Rest", isRest = true))
    }

    suspend fun importPlanFromText(
        name: String,
        text: String,
        assignToAllWeekdays: Boolean = true,
    ): Long {
        val parsed = PlanTextParser.parse(text)
        val planId = createPlan(
            name = name,
            isRest = false,
            assignToAllWeekdays = assignToAllWeekdays,
        )
        if (parsed.isNotEmpty()) {
            exerciseDao.upsertAll(
                parsed.map {
                    ExerciseEntity(
                        planId = planId,
                        name = it.name,
                        detail = it.detail,
                        sortOrder = it.sortOrder,
                    )
                },
            )
        }
        return planId
    }

    suspend fun replaceExercisesFromText(planId: Long, text: String) {
        val plan = planDao.getById(planId) ?: return
        if (plan.isRest) return
        val parsed = PlanTextParser.parse(text)
        exerciseDao.deleteForPlan(planId)
        if (parsed.isNotEmpty()) {
            exerciseDao.upsertAll(
                parsed.map {
                    ExerciseEntity(
                        planId = planId,
                        name = it.name,
                        detail = it.detail,
                        sortOrder = it.sortOrder,
                    )
                },
            )
        }
        recalcAndSave(DateUtils.today()) { it }
    }

    suspend fun addExercise(planId: Long, name: String, detail: String = "") {
        val existing = exerciseDao.getForPlan(planId)
        exerciseDao.upsert(
            ExerciseEntity(
                planId = planId,
                name = name.trim(),
                detail = detail.trim(),
                sortOrder = existing.size,
            ),
        )
    }

    suspend fun deleteExercise(exerciseId: Long) {
        exerciseDao.delete(exerciseId)
        recalcAndSave(DateUtils.today()) { it }
    }

    suspend fun assignWeekday(weekday: Int, planId: Long) {
        scheduleDao.upsert(ScheduleEntity(weekday = weekday, planId = planId))
        recalcAndSave(DateUtils.today()) { it }
    }

    private suspend fun recalcAndSave(
        date: String,
        mutate: (DailyEntryEntity) -> DailyEntryEntity,
    ) {
        val localDate = DateUtils.parse(date)
        val plans = planDao.getAll()
        val schedule = scheduleDao.getAll()
        val planEntity = resolvePlanEntity(localDate, plans, schedule)
        val exercises = if (planEntity != null && !planEntity.isRest) {
            exerciseDao.getForPlan(planEntity.id)
        } else {
            emptyList()
        }
        val checkoffs = checkoffDao.getForDate(date).associate { it.exerciseId to it.completed }
        val completed = exercises.count { checkoffs[it.id] == true }
        val workoutPts = ScoreCalculator.workoutPoints(
            isRestDay = planEntity?.isRest == true,
            completed = completed,
            total = exercises.size,
        )
        val current = mutate(dailyDao.getByDate(date) ?: DailyEntryEntity(date = date))
        val score = ScoreCalculator.calculate(
            workoutPoints = workoutPts,
            sleepHours = current.sleepHours,
            proteinGrams = current.proteinGrams,
            waterLiters = current.waterLiters,
            disciplineCheck = current.disciplineCheck,
        )
        dailyDao.upsert(current.copy(workoutPoints = workoutPts, score = score))
    }

    private fun buildSnapshot(
        date: String,
        entry: DailyEntryEntity?,
        plan: WorkoutPlan?,
        exercises: List<ExerciseEntity>,
        checkoffs: List<CheckoffEntity>,
    ): TodaySnapshot {
        val checkMap = checkoffs.associate { it.exerciseId to it.completed }
        val items = exercises.map { ex ->
            ChecklistItem(
                exerciseId = ex.id,
                name = ex.name,
                detail = ex.detail,
                completed = checkMap[ex.id] == true,
            )
        }
        val isRest = plan?.isRest == true
        val completed = items.count { it.completed }
        val workoutPts = ScoreCalculator.workoutPoints(
            isRestDay = isRest,
            completed = completed,
            total = items.size,
        )
        val base = entry ?: DailyEntryEntity(date = date)
        val total = ScoreCalculator.calculate(
            workoutPoints = workoutPts,
            sleepHours = base.sleepHours,
            proteinGrams = base.proteinGrams,
            waterLiters = base.waterLiters,
            disciplineCheck = base.disciplineCheck,
        )
        return TodaySnapshot(
            entry = DailyEntry(
                date = date,
                sleepHours = base.sleepHours,
                proteinGrams = base.proteinGrams,
                waterLiters = base.waterLiters,
                disciplineCheck = base.disciplineCheck,
                score = total,
            ),
            workout = TodayWorkout(
                plan = plan,
                items = items,
                completedCount = completed,
                totalCount = items.size,
                workoutPoints = workoutPts,
                isRestDay = isRest,
            ),
        )
    }

    private fun resolvePlanEntity(
        date: LocalDate,
        plans: List<PlanEntity>,
        schedule: List<ScheduleEntity>,
    ): PlanEntity? {
        if (plans.isEmpty()) return null
        val byId = plans.associateBy { it.id }
        val weekday = date.dayOfWeek.value
        val assignedId = schedule.firstOrNull { it.weekday == weekday }?.planId
        if (assignedId != null) return byId[assignedId]
        val training = plans.filter { !it.isRest }
        return training.firstOrNull() ?: plans.firstOrNull()
    }

    private fun PlanEntity.toDomain(exercises: List<ExerciseEntity>) = WorkoutPlan(
        id = id,
        name = name,
        isRest = isRest,
        exercises = exercises.map {
            PlanExercise(
                id = it.id,
                planId = it.planId,
                name = it.name,
                detail = it.detail,
                sortOrder = it.sortOrder,
            )
        },
    )
}

private data class TodayInputs(
    val entry: DailyEntryEntity?,
    val plans: List<PlanEntity>,
    val schedule: List<ScheduleEntity>,
    val checkoffs: List<CheckoffEntity>,
)
