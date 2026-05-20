package com.rudra.financeflowpro.domain.model

data class Transaction(
    val id: Long = 0,
    val accountId: Long,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val subcategory: String = "",
    val description: String = "",
    val date: Long,
    val time: Long = 0,
    val toAccountId: Long? = null,
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null,
    val tags: String = "",
    val note: String = "",
    val createdAt: Long
)

enum class TransactionType(val displayName: String) {
    INCOME("Income"),
    EXPENSE("Expense"),
    TRANSFER("Transfer")
}
