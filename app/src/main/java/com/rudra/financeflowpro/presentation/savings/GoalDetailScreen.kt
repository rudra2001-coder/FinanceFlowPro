package com.rudra.financeflowpro.presentation.savings

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.SavingsGoalRepository
import com.rudra.financeflowpro.domain.usecase.SavingsCalculatorUseCase
import com.rudra.financeflowpro.util.CurrencyFormatter
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val savingsGoalRepository: SavingsGoalRepository,
    private val accountRepository: AccountRepository,
    private val calculator: SavingsCalculatorUseCase
) : ViewModel() {

    private val _goal = MutableStateFlow<SavingsGoal?>(null)
    val goal: StateFlow<SavingsGoal?> = _goal.asStateFlow()

    private val _linkedAccount = MutableStateFlow<Account?>(null)
    val linkedAccount: StateFlow<Account?> = _linkedAccount.asStateFlow()

    private val _contributionAmount = MutableStateFlow("")
    val contributionAmount: StateFlow<String> = _contributionAmount.asStateFlow()

    private val _isContributing = MutableStateFlow(false)
    val isContributing: StateFlow<Boolean> = _isContributing.asStateFlow()

    val dailyRequired: Double get() = goal.value?.let { calculator.dailyAmountRequired(it) } ?: 0.0

    fun loadGoal(id: Long) {
        viewModelScope.launch {
            savingsGoalRepository.getGoalById(id).collect { g ->
                _goal.value = g
                g?.linkedAccountId?.let { aid ->
                    accountRepository.getAccountById(aid).collect { _linkedAccount.value = it }
                }
            }
        }
    }

    fun updateContributionAmount(v: String) { _contributionAmount.value = v }

    fun addContribution(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isContributing.value = true
            val amount = _contributionAmount.value.toDoubleOrNull() ?: return@launch
            val g = _goal.value ?: return@launch

            savingsGoalRepository.addContribution(g.id, amount)

            // Deduct from linked account if set
            g.linkedAccountId?.let { aid -> accountRepository.updateBalance(aid, -amount) }

            _contributionAmount.value = ""
            _isContributing.value = false
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: Long,
    onNavigateBack: () -> Unit,
    viewModel: GoalDetailViewModel = hiltViewModel()
) {
    val goal by viewModel.goal.collectAsState()
    val linkedAccount by viewModel.linkedAccount.collectAsState()
    val contributionAmount by viewModel.contributionAmount.collectAsState()
    val isContributing by viewModel.isContributing.collectAsState()

    LaunchedEffect(goalId) { viewModel.loadGoal(goalId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal?.name ?: "Goal Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            goal?.let { g ->
                // Circular progress
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { g.progressPercent },
                        modifier = Modifier.size(160.dp),
                        strokeWidth = 12.dp,
                        color = if (g.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(g.progressPercent * 100).toInt()}%", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Complete", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(CurrencyFormatter.format(g.currentAmount), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("of ${CurrencyFormatter.format(g.targetAmount)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(16.dp))

                if (g.deadline != null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Deadline", style = MaterialTheme.typography.labelSmall)
                            Text(DateUtils.formatDate(g.deadline), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Daily Needed", style = MaterialTheme.typography.labelSmall)
                            Text(CurrencyFormatter.format(viewModel.dailyRequired), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Remaining", style = MaterialTheme.typography.labelSmall)
                            Text(CurrencyFormatter.format(g.remainingAmount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                linkedAccount?.let { acc ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Linked: ${acc.name}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Add Contribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = contributionAmount,
                                onValueChange = viewModel::updateContributionAmount,
                                label = { Text("Amount") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.addContribution(onSuccess = {}) },
                                enabled = contributionAmount.isNotBlank() && !isContributing
                            ) {
                                if (isContributing) CircularProgressIndicator(Modifier.size(20.dp))
                                else Text("Add")
                            }
                        }
                    }
                }

                if (g.isCompleted) {
                    Spacer(Modifier.height(16.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Goal Completed!", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
