package com.rudra.financeflowpro.presentation.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(
    accountId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditAccountViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val type by viewModel.type.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val isDefault by viewModel.isDefault.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    LaunchedEffect(accountId) {
        if (accountId != null) viewModel.loadAccount(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (accountId == null) "Add Account" else "Edit Account") },
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
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::updateName,
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Account Type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccountType.entries.take(3).forEach { accountType ->
                    FilterChip(
                        selected = type == accountType,
                        onClick = { viewModel.updateType(accountType) },
                        label = { Text(accountType.displayName.take(10)) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccountType.entries.drop(3).forEach { accountType ->
                    FilterChip(
                        selected = type == accountType,
                        onClick = { viewModel.updateType(accountType) },
                        label = { Text(accountType.displayName.take(10)) }
                    )
                }
            }

            OutlinedTextField(
                value = balance,
                onValueChange = viewModel::updateBalance,
                label = { Text(if (accountId == null) "Initial Balance" else "Balance") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Currency:", style = MaterialTheme.typography.labelLarge)
                val currencies = listOf("USD", "EUR", "GBP", "BDT", "INR", "JPY")
                currencies.forEach { c ->
                    FilterChip(
                        selected = currency == c,
                        onClick = { viewModel.updateCurrency(c) },
                        label = { Text(c) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDefault, onCheckedChange = viewModel::updateIsDefault)
                Text("Set as default account")
            }

            val colors = listOf("4CAF50", "2196F3", "FF9800", "E53935", "9C27B0", "00BCD4")
            Text("Color", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { c ->
                    Surface(
                        shape = CircleShape,
                        color = Color(android.graphics.Color.parseColor("#$c")),
                        modifier = Modifier.size(36.dp),
                        onClick = { viewModel.updateColor(c) }
                    ) {
                        if (viewModel.color.collectAsState().value == c) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(if (accountId == null) "Create Account" else "Save Changes")
                }
            }
        }
    }
}
