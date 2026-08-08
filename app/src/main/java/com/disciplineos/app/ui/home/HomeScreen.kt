package com.disciplineos.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.disciplineos.app.domain.ScoreCalculator
import com.disciplineos.app.ui.components.ExerciseCheckRow
import com.disciplineos.app.ui.components.HabitSliderRow
import com.disciplineos.app.ui.components.HabitToggleRow
import com.disciplineos.app.ui.components.ScoreHero
import com.disciplineos.app.ui.components.SectionLabel
import com.disciplineos.app.ui.components.SessionQuote
import com.disciplineos.app.ui.theme.Accent
import com.disciplineos.app.ui.theme.AccentDim
import com.disciplineos.app.ui.theme.BorderGray
import com.disciplineos.app.ui.theme.SurfaceElevated
import com.disciplineos.app.ui.theme.SurfaceGray
import com.disciplineos.app.ui.theme.TextPrimary
import com.disciplineos.app.ui.theme.TextSecondary
import com.disciplineos.app.ui.theme.scoreColor
import com.disciplineos.app.ui.theme.screenAtmosphere
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    sessionQuote: String,
    onOpenPlans: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val entry = state.entry
    val workout = state.workout
    val brand by animateColorAsState(scoreColor(state.entry.score), label = "brand")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenAtmosphere())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "DISCIPLINE OS",
            style = MaterialTheme.typography.labelLarge,
            color = brand,
        )

        SessionQuote(quote = sessionQuote)

        ScoreHero(
            score = state.entry.score,
            verdict = state.verdict,
            feedback = state.feedback,
            dateLabel = state.dateLabel,
            modifier = Modifier.padding(vertical = 4.dp),
        )

        SectionLabel("WORKOUT")

        when {
            workout.plan == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, AccentDim, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "No plan yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Import your exercises in Plans — paste plain text and go.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Text(
                        text = "OPEN PLANS →",
                        style = MaterialTheme.typography.labelLarge,
                        color = Accent,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable(onClick = onOpenPlans)
                            .padding(vertical = 4.dp),
                    )
                }
            }

            workout.isRestDay -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceGray)
                        .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        text = workout.plan?.name ?: "Rest",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rest day. Full workout credit for following the plan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${state.breakdown.workout}/${ScoreCalculator.WORKOUT_MAX} PTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Accent,
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = workout.plan?.name?.uppercase().orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Accent,
                    )
                    Text(
                        text = "${workout.completedCount}/${workout.totalCount} done  ·  ${state.breakdown.workout}/${ScoreCalculator.WORKOUT_MAX} pts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }

                if (workout.items.isEmpty()) {
                    Text(
                        text = "This plan has no exercises. Add some in Plans.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                } else {
                    workout.items.forEach { item ->
                        ExerciseCheckRow(
                            name = item.name,
                            detail = item.detail,
                            completed = item.completed,
                            onToggle = {
                                viewModel.setExerciseCompleted(item.exerciseId, !item.completed)
                            },
                        )
                    }
                }
            }
        }

        SectionLabel("HABITS")

        HabitSliderRow(
            title = "Sleep",
            valueLabel = String.format(
                "%.1f h  ·  target %.0fh",
                entry.sleepHours,
                ScoreCalculator.SLEEP_TARGET_HOURS,
            ),
            pointsLabel = "${state.breakdown.sleep}/${ScoreCalculator.SLEEP_MAX}",
            value = entry.sleepHours,
            valueRange = 0f..12f,
            steps = 23,
            onValueChange = { viewModel.setSleepHours((it * 2).roundToInt() / 2f) },
        )

        HabitSliderRow(
            title = "Protein",
            valueLabel = "${entry.proteinGrams.roundToInt()} g  ·  target ${ScoreCalculator.PROTEIN_TARGET_GRAMS.roundToInt()}g",
            pointsLabel = "${state.breakdown.protein}/${ScoreCalculator.PROTEIN_MAX}",
            value = entry.proteinGrams,
            valueRange = 0f..200f,
            steps = 39,
            onValueChange = { viewModel.setProteinGrams(it.roundToInt().toFloat()) },
        )

        HabitSliderRow(
            title = "Water",
            valueLabel = String.format(
                "%.1f L  ·  target %.1fL",
                entry.waterLiters,
                ScoreCalculator.WATER_TARGET_LITERS,
            ),
            pointsLabel = "${state.breakdown.water}/${ScoreCalculator.WATER_MAX}",
            value = entry.waterLiters,
            valueRange = 0f..5f,
            steps = 49,
            onValueChange = { viewModel.setWaterLiters((it * 10).roundToInt() / 10f) },
        )

        HabitToggleRow(
            title = "Discipline check",
            subtitle = "Did you stick to the plan today?",
            points = "${state.breakdown.discipline}/${ScoreCalculator.DISCIPLINE_MAX}",
            checked = entry.disciplineCheck,
            onCheckedChange = viewModel::setDisciplineCheck,
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Resets at local midnight. Score ≥ 70 keeps the streak.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}
