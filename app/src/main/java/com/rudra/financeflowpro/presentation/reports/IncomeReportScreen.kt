package com.rudra.financeflowpro.presentation.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.domain.usecase.GetMonthlyReportUseCase
import com.rudra.financeflowpro.domain.usecase.MonthlyReport
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeReportViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase
) : ViewModel() {

    private val _report = MutableStateFlow<MonthlyReport?>(null)
    val report: StateFlow<MonthlyReport?> = _report.asStateFlow()

    private val _dateRange = MutableStateFlow(Pair(DateUtils.getMonthStartMillis(), DateUtils.getMonthEndMillis()))
    val dateRange: StateFlow<Pair<Long, Long>> = _dateRange.asStateFlow()

    fun loadReport() {
        viewModelScope.launch {
            val (start, end) = _dateRange.value
            _report.value = getMonthlyReportUseCase(start, end)
        }
    }

    fun setDateRange(start: Long, end: Long) {
        _dateRange.value = Pair(start, end)
        loadReport()
    }

    init { loadReport() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: IncomeReportViewModel = hiltViewModel()
) {
    val report by viewModel.report.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income Report") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Filter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("This Month", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { showDatePicker = true }) { Text("Custom Range") }
            }

            report?.let { r ->
                // Summary Cards
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Total Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(CurrencyFormatter.format(r.totalIncome), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Card(Modifier.weight(1f)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Transactions", style = MaterialTheme.typography.labelSmall)
                            Text("${r.transactions.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Income by category
                Text("Income by Source", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val incomeTransactions = r.transactions.filter { it.type.name == "INCOME" }
                val byCategory = incomeTransactions.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }

                if (byCategory.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No income recorded", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    byCategory.forEach { (category, total) ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(category, modifier = Modifier.weight(1f))
                                Text(CurrencyFormatter.format(total), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
