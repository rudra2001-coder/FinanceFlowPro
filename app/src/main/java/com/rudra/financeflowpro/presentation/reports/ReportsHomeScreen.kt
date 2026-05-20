package com.rudra.financeflowpro.presentation.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsHomeScreen(
    onNavigateToIncomeReport: () -> Unit,
    onNavigateToExpenseReport: () -> Unit,
    onNavigateToCashFlowReport: () -> Unit,
    onNavigateToInvestmentReport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reports") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select a report type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            ReportCard("Income Report", "Track your income sources", Icons.Default.TrendingUp, onNavigateToIncomeReport)
            ReportCard("Expense Report", "Analyze your spending by category", Icons.Default.ShoppingCart, onNavigateToExpenseReport)
            ReportCard("Cash Flow Report", "Net cash flow over time", Icons.Default.ShowChart, onNavigateToCashFlowReport)
            ReportCard("Investment Report", "Portfolio performance overview", Icons.Default.TrendingUp, onNavigateToInvestmentReport)
        }
    }
}

@Composable
fun ReportCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
