package com.rudra.financeflowpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
)
