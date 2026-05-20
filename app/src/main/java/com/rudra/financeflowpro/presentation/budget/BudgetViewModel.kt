package com.rudra.financeflowpro.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Budget
import com.rudra.financeflowpro.domain.repository.BudgetRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val currentMonth = DateUtils.getCurrentYearMonth()

    val budgets: StateFlow<List<Budget>> = budgetRepository.getBudgetsForMonth(currentMonth)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()

    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()

    init {
        viewModelScope.launch {
            budgets.collect { list ->
                _totalBudget.value = list.sumOf { it.amount }
                _totalSpent.value = list.sumOf { it.spent }
            }
        }

        viewModelScope.launch {
            val start = DateUtils.getMonthStartMillis()
            val end = DateUtils.getMonthEndMillis()
            val expenses = transactionRepository.getExpenseByCategoryOnce(start, end)
            val currentBudgets = budgetRepository.getBudgetsForMonthOnce(currentMonth)
            expenses.forEach { (category, total) ->
                val budget = currentBudgets.find { it.category == category }
                if (budget != null) {
                    budgetRepository.update(budget.copy(spent = total))
                }
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch { budgetRepository.delete(budget) }
    }
}
