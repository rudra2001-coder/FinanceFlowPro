package com.rudra.financeflowpro.domain.model

data class SavingsGoal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Long? = null,
    val linkedAccountId: Long? = null,
    val color: String,
    val icon: String,
    val isCompleted: Boolean = false,
    val autoContributeAmount: Double? = null,
    val autoContributeInterval: String? = null,
    val createdAt: Long
) {
    val progressPercent: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
}
