package com.rudra.financeflowpro.domain.repository

import com.rudra.financeflowpro.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonth(month: String): Flow<List<Budget>>
    suspend fun getBudgetForCategory(month: String, category: String): Budget?
    suspend fun getBudgetsForMonthOnce(month: String): List<Budget>
    suspend fun insert(budget: Budget): Long
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
}
