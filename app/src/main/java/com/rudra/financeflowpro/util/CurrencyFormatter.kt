package com.rudra.financeflowpro.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    private val defaultFormat = NumberFormat.getCurrencyInstance().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun format(amount: Double, currencyCode: String = "USD"): String {
        return try {
            val format = NumberFormat.getCurrencyInstance()
            format.currency = Currency.getInstance(currencyCode)
            format.format(amount)
        } catch (e: Exception) {
            "${currencyCode} ${String.format("%.2f", amount)}"
        }
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> String.format("%.2f", amount)
        }
    }

    fun formatWithSign(amount: Double, currencyCode: String = "USD"): String {
        val formatted = format(amount, currencyCode)
        return if (amount >= 0) "+$formatted" else formatted
    }
}
