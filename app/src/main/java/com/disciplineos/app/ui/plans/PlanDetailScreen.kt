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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.disciplineos.app.ui.theme.Accent
import com.disciplineos.app.ui.theme.Black
import com.disciplineos.app.ui.theme.ScoreRed
import com.disciplineos.app.ui.theme.SurfaceGray
import com.disciplineos.app.ui.theme.TextMuted
import com.disciplineos.app.ui.theme.TextPrimary
import com.disciplineos.app.ui.theme.TextSecondary
import com.disciplineos.app.ui.theme.screenAtmosphere

@Composable
fun PlanDetailScreen(
    onBack: () -> Unit,
    viewModel: PlanDetailViewModel = hiltViewModel(),
) {
    val plan by viewModel.plan.collectAsStateWithLifecycle()
    var showImport by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var rename by remember(plan?.name) { mutableStateOf(plan?.name.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenAtmosphere())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                )
            }
            Text(
                text = "EDIT PLAN",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted,
            )
        }

        if (plan == null) {
            Text("Plan not found", color = TextSecondary)
            return
        }

        val current = plan!!

        OutlinedTextField(
            value = rename,
            onValueChange = { rename = it },
            label = { Text("Name") },
            singleLine = true,
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "SAVE NAME",
            style = MaterialTheme.typography.labelLarge,
            color = Accent,
            modifier = Modifier
                .clickable { viewModel.rename(rename) }
                .padding(vertical = 4.dp),
        )

        if (current.isRest) {
            Text(
                text = "Rest plans have no exercises. Assign this day on the week schedule.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PASTE LIST",
                    style = MaterialTheme.typography.labelLarge,
                    color = Black,
                    modifier = Modifier
                        .background(Accent, RoundedCornerShape(8.dp))
                        .clickable { showImport = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                )
                Text(
                    text = "ADD ONE",
                    style = MaterialTheme.typography.labelLarge,
                    color = Accent,
                    modifier = Modifier
                        .background(SurfaceGray, RoundedCornerShape(8.dp))
                        .clickable { showAdd = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }

            Text(
                text = "EXERCISES",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted,
                modifier = Modifier.padding(top = 8.dp),
            )

            if (current.exercises.isEmpty()) {
                Text(
                    text = "Empty. Paste your plan text or add exercises.",
                    color = TextSecondary,
                )
            }

            current.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        if (exercise.detail.isNotBlank()) {
                            Text(exercise.detail, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    IconButton(onClick = { viewModel.deleteExercise(exercise.id) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = ScoreRed)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DELETE PLAN",
            style = MaterialTheme.typography.labelLarge,
            color = ScoreRed,
            modifier = Modifier
                .clickable {
                    viewModel.deletePlan(onDone = onBack)
                }
                .padding(vertical = 8.dp),
        )
    }

    if (showImport) {
        var text by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImport = false },
            containerColor = SurfaceGray,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Replace exercises") },
            text = {
                Column {
                    Text("This replaces the current list.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        minLines = 6,
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importText(text)
                        showImport = false
                    },
                    enabled = text.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Black),
                ) { Text("Replace") }
            },
            dismissButton = {
                TextButton(onClick = { showImport = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
        )
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var detail by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            containerColor = SurfaceGray,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Add exercise") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = detail,
                        onValueChange = { detail = it },
                        label = { Text("Detail (e.g. 4x8)") },
                        singleLine = true,
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addExercise(name, detail)
                        showAdd = false
                    },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Black),
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
        )
    }
}
