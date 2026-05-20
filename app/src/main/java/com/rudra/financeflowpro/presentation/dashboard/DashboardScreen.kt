package com.rudra.financeflowpro.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAccount: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToAccountDetail: (Long) -> Unit = {},
    onNavigateToGoalDetail: (Long) -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val netWorth by viewModel.totalNetWorth.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val healthScore by viewModel.healthScore.collectAsState()
    val monthSummary by viewModel.monthSummary.collectAsState()
    val lastMonthExpense by viewModel.lastMonthExpense.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (netWorth >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Total Net Worth", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(
                    CurrencyFormatter.format(netWorth),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        monthSummary?.let { summary ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Income", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32))
                        Text(
                            CurrencyFormatter.format(summary.totalIncome),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Expense", style = MaterialTheme.typography.labelMedium, color = Color(0xFFC62828))
                        Text(
                            CurrencyFormatter.format(summary.totalExpense),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(accounts) { account ->
                AccountMiniCard(account, onClick = { onNavigateToAccountDetail(account.id) })
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onNavigateToTransactions) { Text("See All") }
        }
        Spacer(Modifier.height(4.dp))
        recentTransactions.forEach { tx ->
            TransactionRow(tx)
            if (tx != recentTransactions.last()) Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(16.dp))

        if (activeGoals.isNotEmpty()) {
            Text("Savings Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            activeGoals.forEach { goal ->
                SavingsMiniCard(goal, onClick = { onNavigateToGoalDetail(goal.id) })
                Spacer(Modifier.height(4.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onNavigateToInsights() },
            colors = CardDefaults.cardColors(
                containerColor = when {
                    healthScore >= 80 -> Color(0xFF4CAF50)
                    healthScore >= 50 -> Color(0xFFFF9800)
                    else -> Color(0xFFE53935)
                }
            )
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Financial Health", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text("Tap for insights", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "$healthScore",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton("Reports", Icons.Default.Assessment, onClick = onNavigateToReports)
            QuickActionButton("Insights", Icons.Default.Lightbulb, onClick = onNavigateToInsights)
            QuickActionButton("Accounts", Icons.Default.AccountBalance, onClick = onNavigateToAccount)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun AccountMiniCard(account: Account, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(account.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text(
                CurrencyFormatter.format(account.balance, account.currency),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                account.type.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColor = when (transaction.type) {
                TransactionType.INCOME -> Color(0xFF4CAF50)
                TransactionType.EXPENSE -> Color(0xFFE53935)
                TransactionType.TRANSFER -> Color(0xFF2196F3)
            }
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    when (transaction.type) {
                        TransactionType.INCOME -> Icons.Default.TrendingUp
                        TransactionType.EXPENSE -> Icons.Default.TrendingDown
                        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(transaction.category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(DateUtils.formatDate(transaction.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                CurrencyFormatter.format(if (transaction.type == TransactionType.EXPENSE) -transaction.amount else transaction.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.EXPENSE) Color(0xFFE53935) else Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun SavingsMiniCard(goal: SavingsGoal, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.weight(1f).height(8.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(goal.name, style = MaterialTheme.typography.bodySmall)
                Text(
                    "${(goal.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.padding(12.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
