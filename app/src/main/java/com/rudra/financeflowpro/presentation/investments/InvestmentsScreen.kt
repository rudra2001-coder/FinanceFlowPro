package com.rudra.financeflowpro.presentation.investments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.domain.model.Investment
import com.rudra.financeflowpro.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentsScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: InvestmentsViewModel = hiltViewModel()
) {
    val investments by viewModel.investments.collectAsState()
    val totalInvested by viewModel.totalInvested.collectAsState()
    val totalCurrentValue by viewModel.totalCurrentValue.collectAsState()
    val totalGainLoss by viewModel.totalGainLoss.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investments") },
                actions = { IconButton(onClick = onNavigateToAdd) { Icon(Icons.Default.Add, "Add") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Portfolio Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalGainLoss >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Portfolio Value", style = MaterialTheme.typography.titleMedium)
                        Text(CurrencyFormatter.format(totalCurrentValue),
                            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Row {
                            Text("Invested: ${CurrencyFormatter.format(totalInvested)}",
                                style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${if (totalGainLoss >= 0) "+" else ""}${CurrencyFormatter.format(totalGainLoss)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (totalGainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                            )
                        }
                    }
                }
            }

            if (investments.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text("No investments yet", style = MaterialTheme.typography.bodyLarge)
                            TextButton(onClick = onNavigateToAdd) { Text("Add your first investment") }
                        }
                    }
                }
            } else {
                items(investments) { inv ->
                    InvestmentCard(inv, onClick = { onNavigateToDetail(inv.id) }, onEdit = { onNavigateToEdit(inv.id) }, onDelete = { viewModel.deleteInvestment(inv) })
                }
            }
        }
    }
}

@Composable
fun InvestmentCard(investment: Investment, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(investment.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(investment.type.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(CurrencyFormatter.format(investment.currentValue),
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${if (investment.gainLoss >= 0) "+" else ""}${String.format("%.1f", investment.gainLossPercent)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (investment.gainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
