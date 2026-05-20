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
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel()
) {
    val type by viewModel.type.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val category by viewModel.category.collectAsState()
    val subcategory by viewModel.subcategory.collectAsState()
    val description by viewModel.description.collectAsState()
    val accountId by viewModel.accountId.collectAsState()
    val date by viewModel.date.collectAsState()
    val note by viewModel.note.collectAsState()
    val isRecurring by viewModel.isRecurring.collectAsState()
    val recurringInterval by viewModel.recurringInterval.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) viewModel.loadTransaction(transactionId)
        if (accountId == null && accounts.isNotEmpty()) viewModel.updateAccountId(accounts.first().id)
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
                title = { Text(if (transactionId == null) "Add Transaction" else "Edit Transaction") },
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
            // Type toggle
            Text("Transaction Type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionType.entries.forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { viewModel.updateType(t) },
                        label = { Text(t.displayName) },
                        leadingIcon = {
                            Icon(
                                when (t) {
                                    TransactionType.INCOME -> Icons.Default.TrendingUp
                                    TransactionType.EXPENSE -> Icons.Default.TrendingDown
                                    TransactionType.TRANSFER -> Icons.Default.SwapHoriz
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Account
            if (accounts.isNotEmpty()) {
                Text("Account", style = MaterialTheme.typography.labelLarge)
                accounts.forEach { acc ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = accountId == acc.id,
                            onClick = { viewModel.updateAccountId(acc.id) }
                        )
                        Text(acc.name)
                    }
                }
            }

            // Category
            Text("Category", style = MaterialTheme.typography.labelLarge)
            val categories = viewModel.categories
            Column {
                categories.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { viewModel.updateCategory(cat) },
                                label = { Text(cat.take(12)) }
                            )
                        }
                    }
                }
            }

            // Subcategory
            if (viewModel.subcategories.isNotEmpty()) {
                Text("Subcategory", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.subcategories.take(5).forEach { sub ->
                        FilterChip(
                            selected = subcategory == sub,
                            onClick = { viewModel.updateSubcategory(sub) },
                            label = { Text(sub.take(12)) }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date
            OutlinedTextField(
                value = DateUtils.formatDate(date),
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, "Pick Date")
                    }
                }
            )

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = viewModel::updateNote,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Recurring
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = isRecurring, onCheckedChange = viewModel::updateIsRecurring)
                Text("Recurring transaction")
            }

            if (isRecurring) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("DAILY", "WEEKLY", "MONTHLY").forEach { interval ->
                        FilterChip(
                            selected = recurringInterval == interval,
                            onClick = { viewModel.updateRecurringInterval(interval) },
                            label = { Text(interval.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && category.isNotBlank() && accountId != null && !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text(if (transactionId == null) "Add Transaction" else "Save Changes")
            }
        }
    }
}
