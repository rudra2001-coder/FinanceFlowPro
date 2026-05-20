package com.rudra.financeflowpro.presentation.reports

import androidx.compose.foundation.layout.*
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
import com.rudra.financeflowpro.presentation.investments.InvestmentsViewModel
import com.rudra.financeflowpro.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: InvestmentsViewModel = hiltViewModel()
) {
    val investments by viewModel.investments.collectAsState()
    val totalInvested by viewModel.totalInvested.collectAsState()
    val totalCurrentValue by viewModel.totalCurrentValue.collectAsState()
    val totalGainLoss by viewModel.totalGainLoss.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investment Report") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Summary
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalGainLoss >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Portfolio Summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Invested", style = MaterialTheme.typography.labelSmall); Text(CurrencyFormatter.format(totalInvested), fontWeight = FontWeight.Bold) }
                        Column(horizontalAlignment = Alignment.End) { Text("Current Value", style = MaterialTheme.typography.labelSmall); Text(CurrencyFormatter.format(totalCurrentValue), fontWeight = FontWeight.Bold) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Total ${if (totalGainLoss >= 0) "Gain" else "Loss"}: ${CurrencyFormatter.format(totalGainLoss)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (totalGainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
            }

            // Individual Investments
            if (investments.isEmpty()) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No investments to report", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Individual Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                investments.forEach { inv ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(inv.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(inv.type.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(CurrencyFormatter.format(inv.currentValue), fontWeight = FontWeight.Bold)
                                Text(
                                    "${if (inv.gainLoss >= 0) "+" else ""}${String.format("%.1f", inv.gainLossPercent)}%",
                                    color = if (inv.gainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
