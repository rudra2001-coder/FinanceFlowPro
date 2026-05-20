package com.rudra.financeflowpro.presentation.investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Investment
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import com.rudra.financeflowpro.domain.usecase.InvestmentCalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    private val investmentRepository: InvestmentRepository,
    private val calculator: InvestmentCalculatorUseCase
) : ViewModel() {

    val investments: StateFlow<List<Investment>> = investmentRepository.getAllInvestments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalInvested: StateFlow<Double> = investmentRepository.getTotalInvested()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCurrentValue: StateFlow<Double> = investmentRepository.getTotalCurrentValue()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalGainLoss: StateFlow<Double> = combine(totalInvested, totalCurrentValue) { invested, current ->
        current - invested
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun deleteInvestment(investment: Investment) {
        viewModelScope.launch { investmentRepository.delete(investment) }
    }
}
