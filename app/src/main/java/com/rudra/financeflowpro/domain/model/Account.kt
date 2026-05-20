package com.rudra.financeflowpro.domain.model

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String,
    val color: String,
    val icon: String,
    val isDefault: Boolean,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

enum class AccountType(val displayName: String) {
    BANK("Bank Account"),
    CASH("Cash"),
    WALLET("Digital Wallet"),
    SAVINGS("Savings Account"),
    INVESTMENT("Investment Account"),
    CREDIT("Credit Card")
}
