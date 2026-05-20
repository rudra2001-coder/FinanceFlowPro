package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class TransactionSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val netAmount: Double
)

class GetTransactionSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<TransactionSummary?> {
        return combine(
            repository.getIncomeInRange(startDate, endDate),
            repository.getExpenseInRange(startDate, endDate)
        ) { income, expense ->
            TransactionSummary(income ?: 0.0, expense ?: 0.0, (income ?: 0.0) - (expense ?: 0.0))
        }
    }

    suspend fun getSummaryOnce(startDate: Long, endDate: Long): TransactionSummary {
        val income = repository.getTotalIncomeOnce(startDate, endDate) ?: 0.0
        val expense = repository.getTotalExpenseOnce(startDate, endDate) ?: 0.0
        return TransactionSummary(income, expense, income - expense)
    }
}
