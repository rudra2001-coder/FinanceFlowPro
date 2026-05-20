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
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _expenseByCategory = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val expenseByCategory: StateFlow<List<Pair<String, Double>>> = _expenseByCategory.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    fun loadReport(startDate: Long = DateUtils.getMonthStartMillis(), endDate: Long = DateUtils.getMonthEndMillis()) {
        viewModelScope.launch {
            val data = transactionRepository.getExpenseByCategoryOnce(startDate, endDate)
            _expenseByCategory.value = data
            _totalExpense.value = data.sumOf { it.second }
        }
    }

    init { loadReport() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val expenseByCategory by viewModel.expenseByCategory.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Report") },
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
            Text("This Month", style = MaterialTheme.typography.titleSmall)
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Total Expense", style = MaterialTheme.typography.labelMedium, color = Color(0xFFC62828))
                    Text(CurrencyFormatter.format(totalExpense), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                }
            }

            if (expenseByCategory.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No expenses recorded this month", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                expenseByCategory.forEach { (category, amount) ->
                    val percent = if (totalExpense > 0) (amount / totalExpense) * 100 else 0.0
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(category, style = MaterialTheme.typography.bodyMedium)
                                Text(CurrencyFormatter.format(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = { (percent / 100).toFloat() },
                                    modifier = Modifier.weight(1f).height(6.dp),
                                    color = when {
                                        percent > 30 -> Color(0xFFE53935)
                                        percent > 15 -> Color(0xFFFF9800)
                                        else -> Color(0xFF4CAF50)
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("${String.format("%.1f", percent)}%", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
