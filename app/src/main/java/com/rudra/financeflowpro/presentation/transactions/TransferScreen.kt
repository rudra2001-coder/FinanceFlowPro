package com.rudra.financeflowpro.presentation.transactions

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
import com.rudra.financeflowpro.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val fromAccountId by viewModel.fromAccountId.collectAsState()
    val toAccountId by viewModel.toAccountId.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()
    val date by viewModel.date.collectAsState()
    val isTransferring by viewModel.isTransferring.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(accounts) {
        if (accounts.size >= 2) {
            if (fromAccountId == null) viewModel.updateFromAccount(accounts[0].id)
            if (toAccountId == null) viewModel.updateToAccount(accounts[1].id)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Between Accounts") },
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
            Text("From Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            accounts.forEach { acc ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = fromAccountId == acc.id,
                        onClick = { viewModel.updateFromAccount(acc.id) }
                    )
                    Text("${acc.name} - ${acc.type.displayName}")
                }
            }

            Divider()

            Text("To Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            accounts.forEach { acc ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = toAccountId == acc.id,
                        onClick = { viewModel.updateToAccount(acc.id) },
                        enabled = acc.id != fromAccountId
                    )
                    Text("${acc.name} - ${acc.type.displayName}")
                }
            }

            Divider()

            OutlinedTextField(
                value = amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = DateUtils.formatDate(date),
                onValueChange = {},
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, "Pick Date")
                    }
                }
            )

            OutlinedTextField(
                value = note,
                onValueChange = viewModel::updateNote,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.transfer(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = fromAccountId != null && toAccountId != null && fromAccountId != toAccountId
                        && amount.isNotBlank() && !isTransferring
            ) {
                if (isTransferring) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Transfer")
            }
        }
    }
}
