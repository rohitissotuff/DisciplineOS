package com.disciplineos.app.ui.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.disciplineos.app.data.repository.DisciplineRepository
import com.disciplineos.app.domain.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlansUiState(
    val plans: List<WorkoutPlan> = emptyList(),
    val schedule: Map<Int, Long> = emptyMap(),
)

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val repository: DisciplineRepository,
) : ViewModel() {

    val plans: StateFlow<List<WorkoutPlan>> = repository.observePlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val schedule: StateFlow<Map<Int, Long>> = repository.observeSchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun createEmptyPlan(name: String, assignAllDays: Boolean) {
        viewModelScope.launch {
            repository.createPlan(name = name, assignToAllWeekdays = assignAllDays)
        }
    }

    fun importPlan(name: String, text: String, assignAllDays: Boolean) {
        viewModelScope.launch {
            repository.importPlanFromText(name = name, text = text, assignToAllWeekdays = assignAllDays)
        }
    }

    fun ensureRestPlan() {
        viewModelScope.launch { repository.ensureRestPlan() }
    }

    fun assignWeekday(weekday: Int, planId: Long) {
        viewModelScope.launch { repository.assignWeekday(weekday, planId) }
    }

    fun deletePlan(planId: Long) {
        viewModelScope.launch { repository.deletePlan(planId) }
    }
}

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DisciplineRepository,
) : ViewModel() {

    private val planId: Long = checkNotNull(savedStateHandle.get<Long>("planId"))

    val plan: StateFlow<WorkoutPlan?> = repository.observePlan(planId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun rename(name: String) {
        viewModelScope.launch { repository.renamePlan(planId, name) }
    }

    fun addExercise(name: String, detail: String) {
        viewModelScope.launch { repository.addExercise(planId, name, detail) }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch { repository.deleteExercise(exerciseId) }
    }

    fun importText(text: String) {
        viewModelScope.launch { repository.replaceExercisesFromText(planId, text) }
    }

    fun deletePlan(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deletePlan(planId)
            onDone()
        }
    }
}
