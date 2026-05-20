package com.rudra.financeflowpro.presentation.investments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.domain.model.InvestmentType
import com.rudra.financeflowpro.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInvestmentScreen(
    investmentId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditInvestmentViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val type by viewModel.type.collectAsState()
    val amountInvested by viewModel.amountInvested.collectAsState()
    val currentValue by viewModel.currentValue.collectAsState()
    val units by viewModel.units.collectAsState()
    val buyPrice by viewModel.buyPrice.collectAsState()
    val linkedAccountId by viewModel.linkedAccountId.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val maturityDate by viewModel.maturityDate.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showMaturityDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(investmentId) {
        if (investmentId != null) viewModel.loadInvestment(investmentId)
    }

    @Composable
    fun DatePickerDialogComposable(show: Boolean, initialDate: Long, onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
        if (show) {
            val state = rememberDatePickerState(initialSelectedDateMillis = initialDate)
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = { state.selectedDateMillis?.let(onConfirm); onDismiss() }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
            ) { DatePicker(state = state) }
        }
    }

    DatePickerDialogComposable(showStartDatePicker, startDate, { viewModel.updateStartDate(it) }, { showStartDatePicker = false })
    DatePickerDialogComposable(showMaturityDatePicker, maturityDate ?: System.currentTimeMillis(), { viewModel.updateMaturityDate(it) }, { showMaturityDatePicker = false })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (investmentId == null) "Add Investment" else "Edit Investment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = viewModel::updateName, label = { Text("Investment Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("Type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InvestmentType.entries.take(4).forEach { t ->
                    FilterChip(selected = type == t, onClick = { viewModel.updateType(t) }, label = { Text(t.displayName.take(10)) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InvestmentType.entries.drop(4).forEach { t ->
                    FilterChip(selected = type == t, onClick = { viewModel.updateType(t) }, label = { Text(t.displayName.take(10)) })
                }
            }

            OutlinedTextField(value = amountInvested, onValueChange = viewModel::updateAmountInvested, label = { Text("Amount Invested") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = currentValue, onValueChange = viewModel::updateCurrentValue, label = { Text("Current Value") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = units, onValueChange = viewModel::updateUnits, label = { Text("Units (optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = buyPrice, onValueChange = viewModel::updateBuyPrice, label = { Text("Buy Price (optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(
                value = DateUtils.formatDate(startDate),
                onValueChange = {},
                label = { Text("Start Date") },
                readOnly = true, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { showStartDatePicker = true }) { Icon(Icons.Default.DateRange, null) } }
            )

            OutlinedTextField(
                value = if (maturityDate != null) DateUtils.formatDate(maturityDate!!) else "No maturity date",
                onValueChange = {},
                label = { Text("Maturity Date (optional)") },
                readOnly = true, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { showMaturityDatePicker = true }) { Icon(Icons.Default.DateRange, null) } }
            )

            if (accounts.isNotEmpty()) {
                Text("Linked Account (optional)", style = MaterialTheme.typography.labelLarge)
                accounts.forEach { acc ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = linkedAccountId == acc.id, onClick = { viewModel.updateLinkedAccountId(acc.id) })
                        Text(acc.name)
                    }
                }
            }

            OutlinedTextField(value = notes, onValueChange = viewModel::updateNotes, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && amountInvested.isNotBlank() && currentValue.isNotBlank() && !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                else Text(if (investmentId == null) "Add Investment" else "Save Changes")
            }
        }
    }
}
