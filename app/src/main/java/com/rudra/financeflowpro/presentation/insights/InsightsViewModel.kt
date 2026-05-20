package com.rudra.financeflowpro.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.*
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.domain.usecase.GetFinancialHealthScoreUseCase
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val investmentRepository: InvestmentRepository,
    private val healthScoreUseCase: GetFinancialHealthScoreUseCase
) : ViewModel() {

    val healthScore: StateFlow<Int> = healthScoreUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _spendingProfile = MutableStateFlow<SpendingProfile?>(null)
    val spendingProfile: StateFlow<SpendingProfile?> = _spendingProfile.asStateFlow()

    private val _tips = MutableStateFlow<List<FinancialTip>>(emptyList())
    val tips: StateFlow<List<FinancialTip>> = _tips.asStateFlow()

    private val valuableCategories = setOf("Education", "Investment", "Savings", "Health", "Insurance")
    private val necessaryCategories = setOf("Groceries", "Rent", "Electricity", "Water", "Gas", "Medicine", "Transport")
    private val discretionaryCategories = setOf("Restaurant", "Shopping", "Entertainment", "Personal Care")

    fun analyzeSpending() {
        viewModelScope.launch {
            val start = DateUtils.getMonthStartMillis()
            val end = DateUtils.getMonthEndMillis()
            val transactions = transactionRepository.getTransactionsInRangeOnce(start, end)
                .filter { it.type == TransactionType.EXPENSE }

            if (transactions.isEmpty()) return@launch

            val total = transactions.sumOf { it.amount }
            if (total == 0.0) return@launch

            val byCategory = transactions.groupBy { it.category }

            val necessary = byCategory.filterKeys { it in necessaryCategories }
                .values.sumOf { it.sumOf { t -> t.amount } }
            val valuable = byCategory.filterKeys { it in valuableCategories }
                .values.sumOf { it.sumOf { t -> t.amount } }
            val discretionary = byCategory.filterKeys { it in discretionaryCategories }
                .values.sumOf { it.sumOf { t -> t.amount } }

            val profile = SpendingProfile(
                necessaryPercent = necessary / total * 100,
                valuablePercent = valuable / total * 100,
                discretionaryPercent = discretionary / total * 100
            )
            _spendingProfile.value = profile

            // Generate tips
            val tipsList = mutableListOf<FinancialTip>()
            val income = transactionRepository.getTotalIncomeOnce(start, end) ?: 0.0
            val savingsRate = if (income > 0) ((income - total) / income) * 100 else 0.0

            if (savingsRate < 10) {
                tipsList.add(FinancialTip(TipType.URGENT, "You are saving less than 10% of income. Try the 50-30-20 rule."))
            }
            if (profile.discretionaryPercent > 40) {
                tipsList.add(FinancialTip(TipType.WARNING, "40%+ spent on discretionary items. Try cutting unnecessary expenses."))
            }
            if (profile.valuablePercent < 10) {
                tipsList.add(FinancialTip(TipType.SUGGESTION, "Less than 10% goes to valuable spending. Consider starting a small SIP or FD."))
            }
            if (savingsRate >= 20) {
                tipsList.add(FinancialTip(TipType.POSITIVE, "Excellent savings rate! Consider investing your surplus for long-term wealth."))
            }
            if (profile.necessaryPercent > 70) {
                tipsList.add(FinancialTip(TipType.WARNING, "Over 70% of spending goes to needs. Try reducing fixed expenses."))
            }

            _tips.value = tipsList
        }
    }

    init { analyzeSpending() }
}
