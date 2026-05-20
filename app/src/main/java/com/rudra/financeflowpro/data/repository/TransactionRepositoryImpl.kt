package com.rudra.financeflowpro.data.repository

import com.rudra.financeflowpro.data.local.TransactionDao
import com.rudra.financeflowpro.data.local.TransactionEntity
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    private fun TransactionEntity.toDomain() = Transaction(
        id = id, accountId = accountId, type = TransactionType.valueOf(type),
        amount = amount, category = category, subcategory = subcategory,
        description = description, date = date, time = time,
        toAccountId = toAccountId, isRecurring = isRecurring,
        recurringInterval = recurringInterval, tags = tags,
        note = note, createdAt = createdAt
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id = id, accountId = accountId, type = type.name,
        amount = amount, category = category, subcategory = subcategory,
        description = description, date = date, time = time,
        toAccountId = toAccountId, isRecurring = isRecurring,
        recurringInterval = recurringInterval, tags = tags,
        note = note, createdAt = createdAt
    )

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByAccount(accountId).map { list -> list.map { it.toDomain() } }

    override fun getTransactionById(id: Long): Flow<Transaction?> =
        transactionDao.getTransactionById(id).map { it?.toDomain() }

    override suspend fun getTransactionByIdOnce(id: Long): Transaction? =
        transactionDao.getTransactionByIdOnce(id)?.toDomain()

    override fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsInRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionsInRangeOnce(startDate: Long, endDate: Long): List<Transaction> =
        transactionDao.getTransactionsInRangeOnce(startDate, endDate).map { it.toDomain() }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).map { list -> list.map { it.toDomain() } }

    override fun getIncomeInRange(startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getIncomeInRange(startDate, endDate)

    override fun getExpenseInRange(startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getExpenseInRange(startDate, endDate)

    override suspend fun getTotalExpenseOnce(startDate: Long, endDate: Long): Double? =
        transactionDao.getTotalExpenseOnce(startDate, endDate)

    override suspend fun getTotalIncomeOnce(startDate: Long, endDate: Long): Double? =
        transactionDao.getTotalIncomeOnce(startDate, endDate)

    override fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<Pair<String, Double>>> =
        transactionDao.getExpenseByCategory(startDate, endDate).map { list ->
            list.map { it.category to it.total }
        }

    override suspend fun getExpenseByCategoryOnce(startDate: Long, endDate: Long): List<Pair<String, Double>> =
        transactionDao.getExpenseByCategoryOnce(startDate, endDate).map { it.category to it.total }

    override suspend fun insert(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) =
        transactionDao.update(transaction.toEntity())

    override suspend fun delete(transaction: Transaction) =
        transactionDao.delete(transaction.toEntity())

    override suspend fun deleteBatch(ids: List<Long>) =
        transactionDao.deleteBatch(ids)

    override suspend fun getRecurringTransactions(): List<Transaction> =
        transactionDao.getRecurringTransactions().map { it.toDomain() }
}
