package com.rudra.financeflowpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val amount: Double,
    val spent: Double = 0.0,
    val month: String,
    val createdAt: Long
)
