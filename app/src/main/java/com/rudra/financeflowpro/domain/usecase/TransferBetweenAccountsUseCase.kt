package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import javax.inject.Inject

class TransferBetweenAccountsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        date: Long,
        note: String
    ) {
        val now = System.currentTimeMillis()

        // Debit from source
        transactionRepository.insert(
            Transaction(
                accountId = fromAccountId,
                type = TransactionType.TRANSFER,
                amount = -amount,
                category = "Transfer Out",
                description = "Transfer to ${getAccountName(toAccountId)}",
                date = date,
                toAccountId = toAccountId,
                note = note,
                createdAt = now
            )
        )

        // Credit to destination
        transactionRepository.insert(
            Transaction(
                accountId = toAccountId,
                type = TransactionType.TRANSFER,
                amount = amount,
                category = "Transfer In",
                description = "Transfer from ${getAccountName(fromAccountId)}",
                date = date,
                toAccountId = fromAccountId,
                note = note,
                createdAt = now
            )
        )

        // Update balances
        accountRepository.updateBalance(fromAccountId, -amount)
        accountRepository.updateBalance(toAccountId, amount)
    }

    private suspend fun getAccountName(accountId: Long): String {
        return accountRepository.getAccountByIdOnce(accountId)?.name ?: "Unknown"
    }
}
