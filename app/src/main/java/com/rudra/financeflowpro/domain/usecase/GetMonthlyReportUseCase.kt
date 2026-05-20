package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import javax.inject.Inject

data class MonthlyReport(
    val totalIncome: Double,
    val totalExpense: Double,
    val netFlow: Double,
    val categoryBreakdown: List<Pair<String, Double>>,
    val transactions: List<Transaction>
)

class GetMonthlyReportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(startDate: Long, endDate: Long): MonthlyReport {
        val transactions = transactionRepository.getTransactionsInRangeOnce(startDate, endDate)
        val income = transactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
        val expense = transactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
        val byCategory = transactions.filter { it.type.name == "EXPENSE" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
            .entries.sortedByDescending { it.value }.map { it.key to it.value }

        return MonthlyReport(
            totalIncome = income,
            totalExpense = expense,
            netFlow = income - expense,
            categoryBreakdown = byCategory,
            transactions = transactions.sortedByDescending { it.date }
        )
    }
}
