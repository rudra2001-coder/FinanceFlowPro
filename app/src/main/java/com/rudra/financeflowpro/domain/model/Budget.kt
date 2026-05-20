package com.rudra.financeflowpro.domain.model

data class Budget(
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val spent: Double = 0.0,
    val month: String,
    val createdAt: Long
) {
    val remaining: Double get() = (amount - spent).coerceAtLeast(0.0)
    val usagePercent: Float get() = if (amount > 0) (spent / amount).toFloat().coerceIn(0f, 1f) else 1f
    val isOverBudget: Boolean get() = spent > amount
}
