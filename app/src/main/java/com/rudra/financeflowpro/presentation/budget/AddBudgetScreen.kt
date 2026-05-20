package com.rudra.financeflowpro.presentation.budget

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddBudgetViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val categories = viewModel.categories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Budget") },
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
            Text("Select Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val rowCategories = categories.chunked(3)
            rowCategories.forEach { row ->
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

            OutlinedTextField(
                value = amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Monthly Budget Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = category.isNotBlank() && amount.isNotBlank() && !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Set Budget")
            }
        }
    }
}
