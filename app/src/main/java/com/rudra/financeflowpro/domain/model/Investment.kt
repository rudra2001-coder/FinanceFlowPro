package com.rudra.financeflowpro.domain.model

data class Investment(
    val id: Long = 0,
    val name: String,
    val type: InvestmentType,
    val amountInvested: Double,
    val currentValue: Double,
    val units: Double? = null,
    val buyPrice: Double? = null,
    val currentPrice: Double? = null,
    val linkedAccountId: Long? = null,
    val startDate: Long,
    val maturityDate: Long? = null,
    val returnRate: Double? = null,
    val notes: String = "",
    val createdAt: Long
) {
    val gainLoss: Double get() = currentValue - amountInvested
    val gainLossPercent: Double
        get() = if (amountInvested > 0) ((currentValue - amountInvested) / amountInvested) * 100 else 0.0
}

enum class InvestmentType(val displayName: String) {
    STOCKS("Stocks"),
    MUTUAL_FUND("Mutual Fund"),
    CRYPTO("Cryptocurrency"),
    GOLD("Gold"),
    BONDS("Bonds"),
    FD("Fixed Deposit"),
    RD("Recurring Deposit"),
    OTHER("Other")
}
