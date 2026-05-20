package com.rudra.financeflowpro.domain.repository

import com.rudra.financeflowpro.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>>
    fun getTransactionById(id: Long): Flow<Transaction?>
    suspend fun getTransactionByIdOnce(id: Long): Transaction?
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun getTransactionsInRangeOnce(startDate: Long, endDate: Long): List<Transaction>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun getIncomeInRange(startDate: Long, endDate: Long): Flow<Double?>
    fun getExpenseInRange(startDate: Long, endDate: Long): Flow<Double?>
    suspend fun getTotalExpenseOnce(startDate: Long, endDate: Long): Double?
    suspend fun getTotalIncomeOnce(startDate: Long, endDate: Long): Double?
    fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<Pair<String, Double>>>
    suspend fun getExpenseByCategoryOnce(startDate: Long, endDate: Long): List<Pair<String, Double>>
    suspend fun insert(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun deleteBatch(ids: List<Long>)
    suspend fun getRecurringTransactions(): List<Transaction>
}
