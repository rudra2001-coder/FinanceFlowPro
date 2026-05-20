package com.rudra.financeflowpro.presentation.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.rudra.financeflowpro.domain.model.Budget
import com.rudra.financeflowpro.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val budgets by viewModel.budgets.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                actions = { IconButton(onClick = onNavigateToAdd) { Icon(Icons.Default.Add, "Set Budget") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalSpent <= totalBudget) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Monthly Budget", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Spent: ${CurrencyFormatter.format(totalSpent)}", fontWeight = FontWeight.Bold)
                            Text("Budget: ${CurrencyFormatter.format(totalBudget)}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        val overallPercent = if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { overallPercent },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (overallPercent > 1f) Color(0xFFE53935) else if (overallPercent > 0.8f) Color(0xFFFF9800) else Color(0xFF4CAF50),
                        )
                        if (totalBudget > 0) {
                            Text(
                                "${String.format("%.0f", totalSpent / totalBudget * 100)}% used",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (budgets.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text("No budgets set", style = MaterialTheme.typography.bodyLarge)
                            TextButton(onClick = onNavigateToAdd) { Text("Set up your first budget") }
                        }
                    }
                }
            } else {
                item { Text("Category Budgets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(budgets) { budget ->
                    BudgetCard(budget, onDelete = { viewModel.deleteBudget(budget) })
                }
            }
        }
    }
}

@Composable
fun BudgetCard(budget: Budget, onDelete: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (budget.isOverBudget) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(budget.category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    "${if (budget.isOverBudget) "Over!" else ""} ${CurrencyFormatter.format(budget.spent)} / ${CurrencyFormatter.format(budget.amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (budget.isOverBudget) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { budget.usagePercent },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = when {
                    budget.usagePercent > 1f -> Color(0xFFE53935)
                    budget.usagePercent > 0.8f -> Color(0xFFFF9800)
                    else -> Color(0xFF4CAF50)
                },
            )
            Text(
                "${String.format("%.0f", budget.usagePercent * 100)}% used - ${CurrencyFormatter.format(budget.remaining)} remaining",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
