package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.FinancialHealthScore
import com.rudra.financeflowpro.domain.model.SpendingProfile
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.domain.repository.BudgetRepository
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetFinancialHealthScoreUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository,
    private val investmentRepository: InvestmentRepository
) {
    operator fun invoke(): Flow<Int> {
        val now = Calendar.getInstance()
        val monthStart = getMonthStartMillis()
        val monthEnd = Calendar.getInstance().timeInMillis

        return combine(
            transactionRepository.getIncomeInRange(monthStart, monthEnd),
            transactionRepository.getExpenseInRange(monthStart, monthEnd),
            accountRepository.getTotalNetWorth(),
            investmentRepository.getTotalInvested()
        ) { income, expense, netWorth, totalInvested ->
            val savingsRate = if ((income ?: 0.0) > 0) (((income ?: 0.0) - (expense ?: 0.0)) / (income ?: 0.0)) * 100 else 0.0
            calculateScore(
                income ?: 0.0, expense ?: 0.0,
                savingsRate = savingsRate,
                hasInvestments = (totalInvested ?: 0.0) > 0,
                hasCreditDebt = (netWorth ?: 0.0) < 0
            ).score
        }
    }

    fun calculateScore(totalIncome: Double, totalExpense: Double, savingsRate: Double = 0.0, budgetAdherence: Double = 1.0, hasInvestments: Boolean = false, hasCreditDebt: Boolean = false): FinancialHealthScore {
        val tips = mutableListOf<String>()

        // Savings Rate (0-25)
        val savingsRateScore = when {
            savingsRate >= 20 -> { tips.add("Excellent savings rate! Consider investing your surplus."); 25 }
            savingsRate >= 10 -> { tips.add("Good savings rate. Try to push it above 20%."); 15 }
            else -> { tips.add("You are saving less than 10% of income. Try the 50-30-20 rule."); 5 }
        }

        // Budget Adherence (0-25)
        val budgetScore = when {
            budgetAdherence >= 0.9 -> 25
            budgetAdherence >= 0.7 -> 15
            else -> { tips.add("You are overspending in several categories. Review your budget."); 5 }
        }

        // Investment Activity (0-15)
        val investmentScore = if (hasInvestments) {
            tips.add("You're investing - great for long-term wealth!");
            15
        } else {
            tips.add("No investment activity detected. Even small SIPs grow significantly over time.");
            0
        }

        // Debt Ratio (0-15)
        val debtScore = if (hasCreditDebt) {
            tips.add("Review your credit usage. Try to keep credit utilization below 30%.");
            5
        } else 15

        // Expense Consistency (0-20) - simplified
        val consistencyScore = 15

        val totalScore = (savingsRateScore + budgetScore + investmentScore + debtScore + consistencyScore)
            .coerceIn(0, 100)

        return FinancialHealthScore(
            score = totalScore,
            savingsRateScore = savingsRateScore,
            budgetAdherenceScore = budgetScore,
            expenseConsistencyScore = consistencyScore,
            debtRatioScore = debtScore,
            investmentActivityScore = investmentScore,
            tips = tips
        )
    }

    private fun getMonthStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
