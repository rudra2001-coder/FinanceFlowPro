package com.rudra.financeflowpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
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
)
