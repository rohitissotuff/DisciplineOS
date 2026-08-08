package com.disciplineos.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.disciplineos.app.domain.ScoreCalculator
import com.disciplineos.app.ui.components.WeeklyBarChart
import com.disciplineos.app.ui.theme.BorderGray
import com.disciplineos.app.ui.theme.ScoreGreen
import com.disciplineos.app.ui.theme.SurfaceElevated
import com.disciplineos.app.ui.theme.SurfaceGray
import com.disciplineos.app.ui.theme.TextMuted
import com.disciplineos.app.ui.theme.TextPrimary
import com.disciplineos.app.ui.theme.TextSecondary
import com.disciplineos.app.ui.theme.scoreColor
import com.disciplineos.app.ui.theme.screenAtmosphere
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val stats by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenAtmosphere())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "DASHBOARD",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )
        Text(
            text = "Accountability",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatBlock(
                label = "STREAK",
                value = stats.streak.toString(),
                hint = "days ≥ ${ScoreCalculator.STREAK_THRESHOLD}",
                accent = if (stats.streak > 0) ScoreGreen else TextMuted,
                modifier = Modifier.weight(1f),
            )
            StatBlock(
                label = "AVERAGE",
                value = stats.averageScore.roundToInt().toString(),
                hint = "this week",
                accent = scoreColor(stats.averageScore.roundToInt()),
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Text(
                text = "LAST 7 DAYS",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyBarChart(
                labels = stats.weeklyScores.map { it.label },
                scores = stats.weeklyScores.map { it.score },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Green 80+ · Yellow 50–79 · Red <50",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Text(
            text = "Consistency beats motivation. The graph doesn't care how you feel.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    hint: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGray)
            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = TextMuted)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.headlineLarge, color = accent)
        Text(text = hint, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
