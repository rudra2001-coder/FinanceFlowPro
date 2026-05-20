package com.rudra.financeflowpro.presentation.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSavingsGoalScreen(
    goalId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditSavingsGoalViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val targetAmount by viewModel.targetAmount.collectAsState()
    val currentAmount by viewModel.currentAmount.collectAsState()
    val deadline by viewModel.deadline.collectAsState()
    val linkedAccountId by viewModel.linkedAccountId.collectAsState()
    val color by viewModel.color.collectAsState()
    val autoContributeAmount by viewModel.autoContributeAmount.collectAsState()
    val autoContributeInterval by viewModel.autoContributeInterval.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(goalId) {
        if (goalId != null) viewModel.loadGoal(goalId)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = deadline ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDeadline(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (goalId == null) "New Savings Goal" else "Edit Goal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = viewModel::updateName, label = { Text("Goal Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = targetAmount, onValueChange = viewModel::updateTargetAmount, label = { Text("Target Amount") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = currentAmount, onValueChange = viewModel::updateCurrentAmount, label = { Text("Current Amount Saved") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Deadline
            OutlinedTextField(
                value = if (deadline != null) DateUtils.formatDate(deadline!!) else "No deadline",
                onValueChange = {},
                label = { Text("Deadline (optional)") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, "Pick Date") }
                }
            )

            // Link Account
            if (accounts.isNotEmpty()) {
                Text("Link Account (optional)", style = MaterialTheme.typography.labelLarge)
                accounts.forEach { acc ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = linkedAccountId == acc.id, onClick = { viewModel.updateLinkedAccountId(acc.id) })
                        Text(acc.name)
                    }
                }
            }

            // Color
            Text("Color", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("2196F3", "4CAF50", "FF9800", "E53935", "9C27B0", "00BCD4").forEach { c ->
                    Surface(
                        shape = CircleShape,
                        color = Color(android.graphics.Color.parseColor("#$c")),
                        modifier = Modifier.size(36.dp),
                        onClick = { viewModel.updateColor(c) }
                    ) {
                        if (color == c) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            Divider()
            Text("Auto-Contribute (optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = autoContributeAmount, onValueChange = viewModel::updateAutoContributeAmount, label = { Text("Amount per contribution") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            if (autoContributeAmount.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("WEEKLY", "MONTHLY").forEach { interval ->
                        FilterChip(
                            selected = autoContributeInterval == interval,
                            onClick = { viewModel.updateAutoContributeInterval(interval) },
                            label = { Text(interval.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && targetAmount.isNotBlank() && !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                else Text(if (goalId == null) "Create Goal" else "Save Changes")
            }
        }
    }
}
