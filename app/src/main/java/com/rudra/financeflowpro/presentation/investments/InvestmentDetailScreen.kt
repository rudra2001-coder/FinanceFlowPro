package com.rudra.financeflowpro.presentation.investments

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
import com.rudra.financeflowpro.domain.model.Investment
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import com.rudra.financeflowpro.domain.usecase.InvestmentCalculatorUseCase
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentDetailViewModel @Inject constructor(
    private val investmentRepository: InvestmentRepository,
    private val calculator: InvestmentCalculatorUseCase
) : ViewModel() {

    private val _investment = MutableStateFlow<Investment?>(null)
    val investment: StateFlow<Investment?> = _investment.asStateFlow()

    private val _newCurrentValue = MutableStateFlow("")
    val newCurrentValue: StateFlow<String> = _newCurrentValue.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    fun loadInvestment(id: Long) {
        viewModelScope.launch {
            investmentRepository.getInvestmentById(id).collect { _investment.value = it }
        }
    }

    fun updateNewCurrentValue(v: String) { _newCurrentValue.value = v }

    fun updateCurrentValue(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isUpdating.value = true
            val inv = _investment.value ?: return@launch
            val newValue = _newCurrentValue.value.toDoubleOrNull() ?: return@launch
            investmentRepository.update(inv.copy(currentValue = newValue))
            _newCurrentValue.value = ""
            _isUpdating.value = false
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    investmentId: Long,
    onNavigateBack: () -> Unit,
    viewModel: InvestmentDetailViewModel = hiltViewModel()
) {
    val investment by viewModel.investment.collectAsState()
    val newCurrentValue by viewModel.newCurrentValue.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()

    LaunchedEffect(investmentId) { viewModel.loadInvestment(investmentId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(investment?.name ?: "Investment Detail") },
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
            investment?.let { inv ->
                // Summary Card
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text(inv.type.displayName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(CurrencyFormatter.format(inv.currentValue), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Invested: ${CurrencyFormatter.format(inv.amountInvested)}",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Gain/Loss Card
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (inv.gainLoss >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Gain / Loss", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${if (inv.gainLoss >= 0) "+" else ""}${CurrencyFormatter.format(inv.gainLoss)}",
                            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                            color = if (inv.gainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935))
                        Text("${String.format("%.2f", inv.gainLossPercent)}%", style = MaterialTheme.typography.titleMedium,
                            color = if (inv.gainLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935))
                    }
                }

                // Details
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("Start Date", DateUtils.formatDate(inv.startDate))
                        if (inv.maturityDate != null) DetailRow("Maturity Date", DateUtils.formatDate(inv.maturityDate))
                        if (inv.units != null) DetailRow("Units", inv.units.toString())
                        if (inv.buyPrice != null) DetailRow("Buy Price", CurrencyFormatter.format(inv.buyPrice))
                        if (inv.notes.isNotBlank()) DetailRow("Notes", inv.notes)
                    }
                }

                // Update Value
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Update Current Value", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newCurrentValue,
                                onValueChange = viewModel::updateNewCurrentValue,
                                label = { Text("New Value") },
                                modifier = Modifier.weight(1f), singleLine = true)
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.updateCurrentValue(onSuccess = {}) },
                                enabled = newCurrentValue.isNotBlank() && !isUpdating
                            ) {
                                if (isUpdating) CircularProgressIndicator(Modifier.size(20.dp))
                                else Text("Update")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
