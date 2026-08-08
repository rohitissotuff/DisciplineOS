package com.disciplineos.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.disciplineos.app.data.repository.DisciplineRepository
import com.disciplineos.app.data.repository.TodaySnapshot
import com.disciplineos.app.domain.DailyEntry
import com.disciplineos.app.domain.DateDisplay
import com.disciplineos.app.domain.DateUtils
import com.disciplineos.app.domain.DayClock
import com.disciplineos.app.domain.FeedbackEngine
import com.disciplineos.app.domain.ScoreBreakdown
import com.disciplineos.app.domain.ScoreCalculator
import com.disciplineos.app.domain.TodayWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val dateKey: String = "",
    val dateLabel: String = "",
    val entry: DailyEntry = DailyEntry(date = ""),
    val workout: TodayWorkout = TodayWorkout(
        plan = null,
        items = emptyList(),
        completedCount = 0,
        totalCount = 0,
        workoutPoints = 0,
        isRestDay = false,
    ),
    val breakdown: ScoreBreakdown = ScoreBreakdown(0, 0, 0, 0, 0, 0),
    val feedback: String = FeedbackEngine.messageFor(0),
    val verdict: String = FeedbackEngine.verdictFor(0),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DisciplineRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = DayClock.observeDateKeys()
        .flatMapLatest { dateKey ->
            repository.observeToday(dateKey).map { snap -> snap.toUiState(dateKey) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(
                dateKey = DateUtils.today(),
                dateLabel = DateDisplay.headerLabel(),
            ),
        )

    fun setSleepHours(value: Float) {
        viewModelScope.launch {
            repository.updateHabits(sleepHours = value, date = currentDate())
        }
    }

    fun setProteinGrams(value: Float) {
        viewModelScope.launch {
            repository.updateHabits(proteinGrams = value, date = currentDate())
        }
    }

    fun setWaterLiters(value: Float) {
        viewModelScope.launch {
            repository.updateHabits(waterLiters = value, date = currentDate())
        }
    }

    fun setDisciplineCheck(value: Boolean) {
        viewModelScope.launch {
            repository.updateHabits(disciplineCheck = value, date = currentDate())
        }
    }

    fun setExerciseCompleted(exerciseId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.setExerciseCompleted(
                exerciseId = exerciseId,
                completed = completed,
                date = currentDate(),
            )
        }
    }

    private fun currentDate(): String =
        uiState.value.dateKey.ifBlank { DateUtils.today() }

    private fun TodaySnapshot.toUiState(dateKey: String): HomeUiState {
        val breakdown = ScoreCalculator.breakdown(
            workoutPoints = workout.workoutPoints,
            sleepHours = entry.sleepHours,
            proteinGrams = entry.proteinGrams,
            waterLiters = entry.waterLiters,
            disciplineCheck = entry.disciplineCheck,
        )
        return HomeUiState(
            dateKey = dateKey,
            dateLabel = DateDisplay.headerLabel(dateKey),
            entry = entry.copy(score = breakdown.total),
            workout = workout,
            breakdown = breakdown,
            feedback = FeedbackEngine.messageFor(breakdown.total),
            verdict = FeedbackEngine.verdictFor(breakdown.total),
        )
    }
}
