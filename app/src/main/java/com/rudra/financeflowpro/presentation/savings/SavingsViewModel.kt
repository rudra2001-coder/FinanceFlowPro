package com.rudra.financeflowpro.presentation.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.SavingsGoalRepository
import com.rudra.financeflowpro.domain.usecase.SavingsCalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val savingsGoalRepository: SavingsGoalRepository,
    private val accountRepository: AccountRepository,
    private val calculator: SavingsCalculatorUseCase
) : ViewModel() {

    val goals: StateFlow<List<SavingsGoal>> = savingsGoalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch { savingsGoalRepository.delete(goal) }
    }
}
