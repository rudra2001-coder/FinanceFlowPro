package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        val id = transactionRepository.insert(transaction)
        if (transaction.type.name == "INCOME") {
            accountRepository.updateBalance(transaction.accountId, transaction.amount)
        } else if (transaction.type.name == "EXPENSE") {
            accountRepository.updateBalance(transaction.accountId, -transaction.amount)
        }
        return id
    }
}
