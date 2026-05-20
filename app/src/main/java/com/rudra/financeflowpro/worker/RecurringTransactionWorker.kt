package com.rudra.financeflowpro.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val recurringTransactions = transactionRepository.getRecurringTransactions()
            val now = System.currentTimeMillis()

            recurringTransactions.forEach { tx ->
                val shouldGenerate = when (tx.recurringInterval) {
                    "DAILY" -> true
                    "WEEKLY" -> now - tx.date >= TimeUnit.DAYS.toMillis(7)
                    "MONTHLY" -> now - tx.date >= TimeUnit.DAYS.toMillis(30)
                    else -> false
                }

                if (shouldGenerate) {
                    val newTx = tx.copy(
                        id = 0,
                        date = now,
                        time = now,
                        createdAt = now
                    )
                    transactionRepository.insert(newTx)

                    when (tx.type) {
                        TransactionType.EXPENSE -> accountRepository.updateBalance(tx.accountId, -tx.amount)
                        TransactionType.INCOME -> accountRepository.updateBalance(tx.accountId, tx.amount)
                        else -> {}
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
