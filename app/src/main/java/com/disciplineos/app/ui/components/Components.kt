package com.disciplineos.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.disciplineos.app.ui.theme.Accent
import com.disciplineos.app.ui.theme.AccentDim
import com.disciplineos.app.ui.theme.BorderGray
import com.disciplineos.app.ui.theme.ScoreGreen
import com.disciplineos.app.ui.theme.SurfaceElevated
import com.disciplineos.app.ui.theme.SurfaceGray
import com.disciplineos.app.ui.theme.TextMuted
import com.disciplineos.app.ui.theme.TextPrimary
import com.disciplineos.app.ui.theme.TextSecondary
import com.disciplineos.app.ui.theme.scoreColor

@Composable
fun ScoreHero(
    score: Int,
    verdict: String,
    feedback: String,
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    val color by animateColorAsState(scoreColor(score), label = "scoreColor")
    val progress by animateFloatAsState(
        targetValue = (score / 100f).coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "scoreProgress",
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val stroke = 14.dp.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset(stroke / 2f, stroke / 2f)
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = BorderGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = color,
                )
                Text(
                    text = "OF 100",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = verdict,
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = feedback,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}

@Composable
fun SessionQuote(quote: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated)
            .border(1.dp, AccentDim, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Accent),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = "SESSION",
                style = MaterialTheme.typography.labelLarge,
                color = Accent,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextMuted,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    )
}

@Composable
fun HabitToggleRow(
    title: String,
    subtitle: String,
    points: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGray)
            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Text(
            text = points,
            style = MaterialTheme.typography.labelLarge,
            color = if (checked) ScoreGreen else TextMuted,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = ScoreGreen,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = BorderGray,
            ),
        )
    }
}

@Composable
fun HabitSliderRow(
    title: String,
    valueLabel: String,
    pointsLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGray)
            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = valueLabel, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Text(
                text = pointsLabel,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = TextPrimary,
                activeTrackColor = Accent,
                inactiveTrackColor = BorderGray,
            ),
        )
    }
}

@Composable
fun ExerciseCheckRow(
    name: String,
    detail: String,
    completed: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (completed) SurfaceElevated else SurfaceGray)
            .border(
                width = 1.dp,
                color = if (completed) ScoreGreen.copy(alpha = 0.35f) else BorderGray,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (completed) ScoreGreen else BorderGray.copy(alpha = 0.5f))
                .border(1.dp, if (completed) ScoreGreen else BorderGray, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (completed) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = if (completed) TextSecondary else TextPrimary,
            )
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
fun WeeklyBarChart(
    labels: List<String>,
    scores: List<Int>,
    modifier: Modifier = Modifier,
) {
    val maxHeight = 140.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight + 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        labels.zip(scores).forEach { (label, score) ->
            val fraction = (score / 100f).coerceIn(0f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height((maxHeight * fraction).coerceAtLeast(4.dp))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(scoreColor(score)),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label.take(1),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted,
                )
            }
        }
    }
}
