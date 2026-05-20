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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CashFlowReportViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _income = MutableStateFlow(0.0)
    val income: StateFlow<Double> = _income.asStateFlow()

    private val _expense = MutableStateFlow(0.0)
    val expense: StateFlow<Double> = _expense.asStateFlow()

    val netFlow: StateFlow<Double> = combine(_income, _expense) { i, e -> i - e }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun loadReport(startDate: Long = DateUtils.getMonthStartMillis(), endDate: Long = DateUtils.getMonthEndMillis()) {
        viewModelScope.launch {
            _income.value = transactionRepository.getTotalIncomeOnce(startDate, endDate) ?: 0.0
            _expense.value = transactionRepository.getTotalExpenseOnce(startDate, endDate) ?: 0.0
        }
    }

    init { loadReport() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: CashFlowReportViewModel = hiltViewModel()
) {
    val income by viewModel.income.collectAsState()
    val expense by viewModel.expense.collectAsState()
    val netFlow by viewModel.netFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cash Flow Report") },
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
            Text("This Month", style = MaterialTheme.typography.titleSmall)

            // Net Flow Card
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (netFlow >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Net Cash Flow", style = MaterialTheme.typography.labelMedium)
                    Text(
                        CurrencyFormatter.format(netFlow),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (netFlow >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            // Income vs Expense
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Income", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                        Text(CurrencyFormatter.format(income), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
                Card(
                    Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Expense", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC62828))
                        Text(CurrencyFormatter.format(expense), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                }
            }

            // Cash flow ratio
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Income/Expense Ratio", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    val ratio = if (expense > 0) income / expense else if (income > 0) Double.MAX_VALUE else 0.0
                    Text(
                        if (ratio == Double.MAX_VALUE) "∞ (No expenses)"
                        else String.format("%.2f", ratio),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when {
                            ratio >= 2.0 -> "Excellent! You're earning much more than spending."
                            ratio >= 1.5 -> "Great! Healthy surplus each month."
                            ratio >= 1.0 -> "Good. You're in positive territory."
                            ratio > 0 -> "Caution: Spending exceeds income."
                            else -> "No financial data yet."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
