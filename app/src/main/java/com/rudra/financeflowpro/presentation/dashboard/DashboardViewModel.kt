package com.rudra.financeflowpro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.SavingsGoalRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.domain.usecase.GetFinancialHealthScoreUseCase
import com.rudra.financeflowpro.domain.usecase.TransactionSummary
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val savingsGoalRepository: SavingsGoalRepository,
    private val financialHealthScoreUseCase: GetFinancialHealthScoreUseCase
) : ViewModel() {

    val totalNetWorth: StateFlow<Double> = accountRepository.getTotalNetWorth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGoals: StateFlow<List<SavingsGoal>> = savingsGoalRepository.getAllGoals()
        .map { it.filter { g -> !g.isCompleted }.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthScore: StateFlow<Int> = financialHealthScoreUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _monthSummary = MutableStateFlow<TransactionSummary?>(null)
    val monthSummary: StateFlow<TransactionSummary?> = _monthSummary.asStateFlow()

    val lastMonthExpense: StateFlow<Double> = MutableStateFlow(0.0)

    init {
        loadMonthSummary()
    }

    private fun loadMonthSummary() {
        viewModelScope.launch {
            val start = DateUtils.getMonthStartMillis()
            val end = DateUtils.getMonthEndMillis()
            val income = transactionRepository.getTotalIncomeOnce(start, end) ?: 0.0
            val expense = transactionRepository.getTotalExpenseOnce(start, end) ?: 0.0
            _monthSummary.value = TransactionSummary(income, expense, income - expense)
        }

        viewModelScope.launch {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.MONTH, -1)
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
            val lastStart = cal.timeInMillis
            cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
            val lastEnd = cal.timeInMillis
            val lastExpense = transactionRepository.getTotalExpenseOnce(lastStart, lastEnd) ?: 0.0
            (lastMonthExpense as MutableStateFlow).value = lastExpense
        }
    }
}
