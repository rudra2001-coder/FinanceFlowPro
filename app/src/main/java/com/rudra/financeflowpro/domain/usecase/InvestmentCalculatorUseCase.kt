package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.Investment
import javax.inject.Inject
import kotlin.math.pow

class InvestmentCalculatorUseCase @Inject constructor() {
    fun absoluteReturn(invested: Double, current: Double): Double = current - invested

    fun absoluteReturnPercent(invested: Double, current: Double): Double {
        return if (invested > 0) ((current - invested) / invested) * 100 else 0.0
    }

    fun cagr(invested: Double, current: Double, years: Double): Double {
        return if (invested > 0 && years > 0) ((current / invested).pow(1.0 / years) - 1) * 100 else 0.0
    }

    fun totalPortfolioValue(investments: List<Investment>): Double {
        return investments.sumOf { it.currentValue }
    }

    fun totalGainLoss(investments: List<Investment>): Double {
        return investments.sumOf { it.currentValue - it.amountInvested }
    }
}
