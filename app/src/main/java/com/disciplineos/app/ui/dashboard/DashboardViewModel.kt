package com.disciplineos.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.disciplineos.app.data.repository.DisciplineRepository
import com.disciplineos.app.domain.DashboardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: DisciplineRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardStats> = repository.observeDashboard()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardStats(
                weeklyScores = emptyList(),
                averageScore = 0f,
                streak = 0,
            ),
        )
}
