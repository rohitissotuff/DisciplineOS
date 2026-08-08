package com.disciplineos.app.ui.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.disciplineos.app.domain.WorkoutPlan
import com.disciplineos.app.ui.theme.Accent
import com.disciplineos.app.ui.theme.Black
import com.disciplineos.app.ui.theme.BorderGray
import com.disciplineos.app.ui.theme.SurfaceGray
import com.disciplineos.app.ui.theme.TextMuted
import com.disciplineos.app.ui.theme.TextPrimary
import com.disciplineos.app.ui.theme.TextSecondary
import com.disciplineos.app.ui.theme.screenAtmosphere
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlansScreen(
    onOpenPlan: (Long) -> Unit,
    viewModel: PlansViewModel = hiltViewModel(),
) {
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val schedule by viewModel.schedule.collectAsStateWithLifecycle()

    var showImport by remember { mutableStateOf(false) }
    var showCreate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureRestPlan()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenAtmosphere())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "PLANS",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )
        Text(
            text = "Your system",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip(label = "IMPORT TEXT", onClick = { showImport = true })
            ActionChip(label = "NEW PLAN", onClick = { showCreate = true })
        }

        Text(
            text = "WEEK SCHEDULE",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = "Assign a plan to each day. Switch when you split Push/Pull/Legs.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        (1..7).forEach { weekday ->
            WeekdayRow(
                weekday = weekday,
                selectedPlanId = schedule[weekday],
                plans = plans,
                onSelect = { planId -> viewModel.assignWeekday(weekday, planId) },
            )
        }

        Text(
            text = "ALL PLANS",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
            modifier = Modifier.padding(top = 8.dp),
        )

        plans.forEach { plan ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceGray, RoundedCornerShape(12.dp))
                    .clickable { onOpenPlan(plan.id) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = if (plan.isRest) "Rest day · full workout credit" else "Tap to edit exercises",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = Accent,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showImport) {
        ImportPlanDialog(
            onDismiss = { showImport = false },
            onConfirm = { name, text, assignAll ->
                viewModel.importPlan(name, text, assignAll)
                showImport = false
            },
        )
    }

    if (showCreate) {
        CreatePlanDialog(
            onDismiss = { showCreate = false },
            onConfirm = { name, assignAll ->
                viewModel.createEmptyPlan(name, assignAll)
                showCreate = false
            },
        )
    }
}

@Composable
private fun ActionChip(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = Black,
        modifier = Modifier
            .background(Accent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    )
}

@Composable
private fun WeekdayRow(
    weekday: Int,
    selectedPlanId: Long?,
    plans: List<WorkoutPlan>,
    onSelect: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val dayName = DayOfWeek.of(weekday).getDisplayName(TextStyle.FULL, Locale.getDefault())
    val selected = plans.firstOrNull { it.id == selectedPlanId }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceGray, RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = dayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(
            text = selected?.name ?: "Not set",
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected != null) Accent else TextMuted,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            plans.forEach { plan ->
                DropdownMenuItem(
                    text = { Text(plan.name) },
                    onClick = {
                        onSelect(plan.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ImportPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, text: String, assignAll: Boolean) -> Unit,
) {
    var name by remember { mutableStateOf("My Plan") }
    var text by remember { mutableStateOf("") }
    var assignAll by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceGray,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Import plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Paste one exercise per line.\nBench Press 4x8",
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plan name") },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Exercises") },
                    minLines = 6,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = assignAll,
                        onCheckedChange = { assignAll = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Accent,
                            uncheckedColor = BorderGray,
                            checkmarkColor = Black,
                        ),
                    )
                    Text("Use for every day this week", color = TextSecondary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim().ifBlank { "My Plan" }, text, assignAll) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Black),
            ) { Text("Import") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
    )
}

@Composable
private fun CreatePlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, assignAll: Boolean) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var assignAll by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceGray,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("New plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (e.g. Push)") },
                    singleLine = true,
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = assignAll,
                        onCheckedChange = { assignAll = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Accent,
                            uncheckedColor = BorderGray,
                            checkmarkColor = Black,
                        ),
                    )
                    Text("Assign to all weekdays", color = TextSecondary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim().ifBlank { "Plan" }, assignAll) },
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Black),
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
    )
}

@Composable
internal fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Accent,
    unfocusedBorderColor = BorderGray,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = TextSecondary,
    unfocusedLabelColor = TextMuted,
    cursorColor = Accent,
    focusedContainerColor = Black,
    unfocusedContainerColor = Black,
)
