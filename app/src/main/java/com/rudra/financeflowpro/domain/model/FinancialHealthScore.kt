package com.rudra.financeflowpro.domain.model

data class FinancialHealthScore(
    val score: Int,
    val savingsRateScore: Int,
    val budgetAdherenceScore: Int,
    val expenseConsistencyScore: Int,
    val debtRatioScore: Int,
    val investmentActivityScore: Int,
    val tips: List<String> = emptyList()
) {
    companion object {
        const val MAX_SCORE = 100
    }
}

data class SpendingProfile(
    val necessaryPercent: Double,
    val valuablePercent: Double,
    val discretionaryPercent: Double,
    val wastefulPercent: Double = 0.0,
    val topWasteCategory: String? = null
)

data class FinancialTip(
    val type: TipType,
    val message: String
)

enum class TipType { URGENT, WARNING, SUGGESTION, POSITIVE }
